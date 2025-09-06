package com.example.questionbank.dto.response;

import com.example.questionbank.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Response DTO for subject information")
public class SubjectResponseDTO extends BaseDTO {

    @ApiModelProperty(value = "Subject name")
    private String name;

    @ApiModelProperty(value = "Subject description")
    private String description;

    @ApiModelProperty(value = "Active status")
    private Boolean isActive;

    @ApiModelProperty(value = "Class information")
    private ClassSummaryDTO classInfo;

    @ApiModelProperty(value = "Number of chapters")
    private Integer chapterCount;
}
