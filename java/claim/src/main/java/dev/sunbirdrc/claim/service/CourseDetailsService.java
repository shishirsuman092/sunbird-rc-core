package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.dto.CourseDetailDTO;
import dev.sunbirdrc.claim.entity.CourseDetails;
import dev.sunbirdrc.claim.repository.CourseDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseDetailsService {

    @Autowired
    private CourseDetailsRepository courseDetailsRepository;

    public String findByCourseKey(CourseDetailDTO courseDetailDTO) {

        return null;
    }
}
