package dev.sunbirdrc.claim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.sunbirdrc.claim.model.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDTO {
    @JsonProperty("councilName")
    private String councilName;

    @JsonProperty("courseName")
    private String courseName;

    @JsonProperty("activityName")
    private String activityName;

    @JsonProperty("category")
    private EntityType category;

    @JsonProperty("courseType")
    private String courseType;
}
