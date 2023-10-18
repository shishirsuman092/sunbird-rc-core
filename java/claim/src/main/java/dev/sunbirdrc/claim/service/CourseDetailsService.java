package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.dto.CourseDetailDTO;
import dev.sunbirdrc.claim.entity.CourseDetails;
import dev.sunbirdrc.claim.repository.CourseDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class CourseDetailsService {

    @Autowired
    private CourseDetailsRepository courseDetailsRepository;

    public List<String> findByCoursesByCouncilName(CourseDetailDTO courseDetailDTO) {
        // validate request
        validateCoursesByCouncilNamePayload(courseDetailDTO);
        // fetch record from DB
        List<String> courseNames = courseDetailsRepository.findAllByCouncilNameAndCategoryAndCourseType(courseDetailDTO.getCouncilName(),
                courseDetailDTO.getCategory().name(), courseDetailDTO.getCourseType());
        if(courseNames != null && !courseNames.isEmpty()) {
            return courseNames;
        }
        throw new RuntimeException("Unable to fetch Courses for provided Council Name");
    }

    public List<String> findByActivityByCouncilNameAndCourseName(CourseDetailDTO courseDetailDTO) {
        // validate request
        validateActivityByCouncilNameAndCourseNamePayload(courseDetailDTO);
        // fetch record from DB
        List<String> activityNames = courseDetailsRepository.findAllByCourseName(courseDetailDTO.getCouncilName(), courseDetailDTO.getCourseName(),
                courseDetailDTO.getCategory().name(), courseDetailDTO.getCourseType());
        if(activityNames != null && !activityNames.isEmpty()) {
            return activityNames;
        }
        throw new RuntimeException("Unable to fetch Activity for provided Council Name and Course Name");
    }

    private void validateActivityByCouncilNameAndCourseNamePayload(CourseDetailDTO courseDetailDTO) {
        if(courseDetailDTO == null) {
            throw new RuntimeException("Invalid Request.");
        }
        if(courseDetailDTO.getCouncilName() == null || courseDetailDTO.getCouncilName().isBlank()) {
            throw new RuntimeException("Missing Council Name.");
        }
        if(courseDetailDTO.getCourseName() == null || courseDetailDTO.getCourseName().isBlank()) {
            throw new RuntimeException("Missing Course Name.");
        }
        if(courseDetailDTO.getCategory() == null) {
            throw new RuntimeException("Missing Category.");
        }
    }

    private void validateCoursesByCouncilNamePayload(CourseDetailDTO courseDetailDTO) {
        if(courseDetailDTO == null) {
            throw new RuntimeException("Invalid Request.");
        }
        if(courseDetailDTO.getCouncilName() == null || courseDetailDTO.getCouncilName().isBlank()) {
           throw new RuntimeException("Missing Council Name.");
        }
        if(courseDetailDTO.getCategory() == null) {
            throw new RuntimeException("Missing Category.");
        }
    }
}
