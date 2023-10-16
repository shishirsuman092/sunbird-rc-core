package dev.sunbirdrc.claim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @JsonProperty("isGoodStanding")
    private Boolean goodStanding;

    @JsonProperty("isForeignVerification")
    private Boolean foreignVerification;

}
