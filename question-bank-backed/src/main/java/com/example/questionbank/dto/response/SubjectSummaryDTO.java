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
@ApiModel(description = "Summary DTO for subject information")
public class SubjectSummaryDTO {

    @ApiModelProperty(value = "Subject ID")
    private Long id;

    @ApiModelProperty(value = "Subject name")
    private String name;

    @ApiModelProperty(value = "Class information")
    private ClassSummaryDTO classInfo;
}