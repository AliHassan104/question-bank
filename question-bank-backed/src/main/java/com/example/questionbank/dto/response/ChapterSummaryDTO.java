package com.example.questionbank.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Summary DTO for chapter information")
public class ChapterSummaryDTO {

    @ApiModelProperty(value = "Chapter ID")
    private Long id;

    @ApiModelProperty(value = "Chapter name")
    private String name;

    @ApiModelProperty(value = "Subject information")
    private SubjectSummaryDTO subjectInfo;
}