package dev.sunbirdrc.claim.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.sunbirdrc.claim.dto.ClaimWithNotesDTO;
import dev.sunbirdrc.claim.dto.ClaimWithSize;
import dev.sunbirdrc.claim.entity.Claim;
import dev.sunbirdrc.claim.entity.ClaimNote;
import dev.sunbirdrc.claim.exception.ClaimAlreadyProcessedException;
import dev.sunbirdrc.claim.exception.InvalidInputException;
import dev.sunbirdrc.claim.exception.ResourceNotFoundException;
import dev.sunbirdrc.claim.exception.UnAuthorizedException;
import dev.sunbirdrc.claim.model.ClaimStatus;
import dev.sunbirdrc.claim.repository.ClaimNoteRepository;
import dev.sunbirdrc.claim.repository.ClaimRepository;
import dev.sunbirdrc.pojos.attestation.Action;
import dev.sunbirdrc.registry.middleware.util.EntityUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static dev.sunbirdrc.claim.contants.AttributeNames.*;
import static dev.sunbirdrc.claim.contants.ErrorMessages.*;
import static dev.sunbirdrc.registry.middleware.util.Constants.USER_ID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimNoteRepository claimNoteRepository;
    private final SunbirdRCClient sunbirdRCClient;
    private final ClaimsAuthorizer claimsAuthorizer;
    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);

    @Autowired
    public ClaimService(ClaimRepository claimRepository, ClaimNoteRepository claimNoteRepository, SunbirdRCClient sunbirdRCClient, ClaimsAuthorizer claimsAuthorizer) {
        this.claimRepository = claimRepository;
        this.claimNoteRepository = claimNoteRepository;
        this.sunbirdRCClient = sunbirdRCClient;
        this.claimsAuthorizer = claimsAuthorizer;
    }

    public Claim save(Claim claim) {
        return claimRepository.save(claim);
    }

    public Optional<Claim> findById(String id) {
        return claimRepository.findById(id);
    }

    public List<Claim> findAll() {
        return claimRepository.findAll();
    }

    public Map<String, Object> findClaimsForAttestor(String entity, JsonNode attestorNode, Pageable pageable) {
        List<Claim> claims = claimRepository.findByAttestorEntity(entity);
        logger.info("Found {} claims to process", claims.size());
        List<Claim> claimsToAttestor = claims.stream()
                .filter(claim -> claimsAuthorizer.isAuthorizedAttestor(claim, attestorNode))
                .collect(Collectors.toList());
        return toMap(claimsToAttestor, pageable);
    }

    public Map<String, Object> findClaimsForAttestorByEntityType(String entity, String entityType, JsonNode attestorNode, Pageable pageable) {
        List<Claim> claims = claimRepository.findByAttestorEntityContainingAndEntityContaining(entity,entityType);
        logger.info("Found {} claims to process", claims.size());
        List<Claim> claimsToAttestor = claims.stream()
                .filter(claim -> claimsAuthorizer.isAuthorizedAttestor(claim, attestorNode))
                .collect(Collectors.toList());
        return toMapAll(claimsToAttestor, pageable);
    }

    public Map<String, Object> findClaimsForAttestor1(String entity, JsonNode attestorNode, Pageable pageable) {
        List<Claim> claims = claimRepository.findByAttestorEntity(entity);
        logger.info("Found {} claims to process", claims.size());
        List<Claim> claimsToAttestor = claims.stream()
                .filter(claim -> claimsAuthorizer.isAuthorizedAttestor(claim, attestorNode))
                .collect(Collectors.toList());
        return toMapAll(claimsToAttestor, pageable);
    }

    public ClaimWithSize findAllClaims() {
        ClaimWithSize claimWithSize = new ClaimWithSize();
        List<Claim> claims = claimRepository.findAll();
        claimWithSize.setTotalClaim(claims.size());
        Long totalOpenClaims = claims.stream()
                .filter(claim -> "OPEN".equals(claim.getStatus()))
                .count();
        Long totalApprovedClaims = claims.stream()
                .filter(claim -> "APPROVED".equals(claim.getStatus()))
                .count();
        Long totalApprovedRejected = claims.stream()
                .filter(claim -> "REJECTED".equals(claim.getStatus()))
                .count();
        claimWithSize.setClaimList(claims);
        claimWithSize.setTotalOpen(totalOpenClaims.intValue());
        claimWithSize.setTotalApproved(totalApprovedClaims.intValue());
        claimWithSize.setTotalRejected(totalApprovedRejected.intValue());
        return claimWithSize;
    }

    public List<Claim> findByRequestorName(String entity, Pageable pageable) {
        List<Claim> claims = claimRepository.findByRequestorName(entity);
        logger.info("Found {} claims to process", claims.size());
        List<Claim> claimsRequestor = claims.stream()
                .collect(Collectors.toList());
        return claimsRequestor;
    }

    private Map<String, Object> toMap(List<Claim> claims, Pageable pageable) {
        Map<String, Object> response = new HashMap<>();
        response.put(TOTAL_PAGES, (int)(Math.ceil(claims.size() * 1.0/pageable.getPageSize())));
        response.put(TOTAL_ELEMENTS, claims.size());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), claims.size());
        if(start > claims.size()) {
            response.put(CONTENT, new ArrayList<>());
            return response;
        }
        response.put(CONTENT, claims.subList(start, end));
        return response;
    }

    private Map<String, Object> toMapAll(List<Claim> claims, Pageable pageable) {
        Map<String, Object> response = new HashMap<>();
        response.put(TOTAL_PAGES, (int)(Math.ceil(claims.size() * 1.0/pageable.getPageSize())));
        response.put(TOTAL_ELEMENTS, claims.size());
        int start = 0;
        int end =  claims.size();
        if(start > claims.size()) {
            response.put(CONTENT, new ArrayList<>());
            return response;
        }
        response.put(CONTENT, claims.subList(start, end));
        return response;
    }
    public Claim attestClaim(String claimId, JsonNode requestBody) {
        Claim claim = findById(claimId).orElseThrow(() -> new ResourceNotFoundException(CLAIM_NOT_FOUND));
        logger.info("Processing claim {}", claim.toString());
        if (claim.isClosed()) {
            throw new ClaimAlreadyProcessedException(CLAIM_IS_ALREADY_PROCESSED);
        }
        JsonNode attestorNode = requestBody.get(ATTESTOR_INFO);
        if (!claimsAuthorizer.isAuthorizedAttestor(claim, attestorNode)) {
            throw new UnAuthorizedException(USER_NOT_AUTHORIZED);
        }
        return updateClaim(requestBody, claim);
    }

    private Claim updateClaim(JsonNode requestBody, Claim claim) {
        logger.info("CredType"+claim.getCredType());
        JsonNode attestorNode = requestBody.get(ATTESTOR_INFO);
        if(requestBody.has(NOTES)) {
            addNotes(requestBody.get(NOTES).asText(), claim, EntityUtil.getFullNameOfTheEntity(attestorNode));
        }
        claim.setAttestedOn(new Date());
        String action = requestBody.get("action").asText();
        if(Action.REJECT_CLAIM.toString().equals(action)){
            claim.setStatus(ClaimStatus.REJECTED.name());
        } else if (Action.GRANT_CLAIM.toString().equals(action)) {
            claim.setStatus(ClaimStatus.APPROVED.name());
        }else if (Action.RAISE_CLAIM.toString().equals(action)) {
            claim.setStatus(ClaimStatus.NEW.name());
        }else
            claim.setStatus(ClaimStatus.OPEN.name());

        claim.setAttestorUserId(requestBody.get(USER_ID).asText());
        return claimRepository.save(claim);
    }

    public void addNotes(String notes, Claim claim, String addedBy) {
        ClaimNote claimNote = new ClaimNote();
        claimNote.setNotes(notes);
        claimNote.setPropertyURI(claim.getPropertyURI());
        claimNote.setEntityId(claim.getEntityId());
        claimNote.setAddedBy(addedBy);
        claimNote.setClaimId(claim.getId());
        claimNoteRepository.save(claimNote);
    }

    public List<ClaimNote> getClaimWithNotes(Claim claim) {
        return claimNoteRepository.findByEntityIdAndClaimId(claim.getEntityId(), claim.getId());
    }

    public ClaimWithNotesDTO generateNotesForTheClaim(Claim claim) {
        List<ClaimNote> notes = getClaimWithNotes(claim);
        ClaimWithNotesDTO claimWithNotesDTO = new ClaimWithNotesDTO();
        claimWithNotesDTO.setNotes(notes);
        claimWithNotesDTO.setClaim(claim);
        return claimWithNotesDTO;
    }

    /**
     * @param entityId
     * @param status
     */
    public void updateOutsideStudentStatus(String entityId, ClaimStatus status) {
        if (!StringUtils.isEmpty(entityId) && status != null) {
            List<Claim> claimList = claimRepository.findByEntityId(entityId);

            if (claimList != null && !claimList.isEmpty()) {
                Claim claim = claimList.get(0);

                if (ClaimStatus.APPROVED.name().equalsIgnoreCase(claim.getOutsideStudentStatus())) {
                    throw new ClaimAlreadyProcessedException("Claim is already approved");
                } else {
                    claim.setOutsideStudentStatus(status.name());
                    claimRepository.save(claim);
                }
            }
        } else {
            throw new InvalidInputException("Entity id or status is not valid");
        }
    }

    public void updateInternalFollowUpStatus(String entityId, String status) {
        if (!StringUtils.isEmpty(entityId) && status != null) {
            List<Claim> claimList = claimRepository.findByEntityId(entityId);

            if (claimList != null && !claimList.isEmpty()) {
                Claim claim = claimList.get(0);

                claim.setFollowUpStatus(status);
                claimRepository.save(claim);
            }
        } else {
            throw new InvalidInputException("Entity id or status is not valid");
        }
    }
}