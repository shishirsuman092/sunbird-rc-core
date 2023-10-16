package dev.sunbirdrc.claim.repository;

import dev.sunbirdrc.claim.entity.CourseDetails;
import dev.sunbirdrc.claim.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseDetailsRepository extends JpaRepository<CourseDetails, Long> {
    List<CourseDetails> findByCouncilNameAndCourseNameAndActivityName(String councilName, String courseName, String activityName);

    @Query("SELECT cd.courseName FROM CourseDetails cd WHERE cd.councilName = :councilName " +
            "and cd.courseName = :courseName and cd.activityName = :activityName " +
            "and cd.isGoodStanding = :isGoodStanding and cd.isForeignVerification = :isForeignVerification " +
            "and cd.status = :status limit 1")
    Optional<String> findCourseKey(String councilName, String courseName, String activityName,
                                      Boolean isGoodStanding, Boolean isForeignVerification, Boolean status);
}

