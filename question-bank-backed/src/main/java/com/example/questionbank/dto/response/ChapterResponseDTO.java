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
@ApiModel(description = "Response DTO for chapter information")
public class ChapterResponseDTO extends BaseDTO {

    @ApiModelProperty(value = "Chapter name")
    private String name;

    @ApiModelProperty(value = "Chapter description")
    private String description;

    @ApiModelProperty(value = "Active status")
    private Boolean isActive;

    @ApiModelProperty(value = "Subject information")
    private SubjectSummaryDTO subjectInfo;

    @ApiModelProperty(value = "Number of questions")
    private Integer questionCount;
}
