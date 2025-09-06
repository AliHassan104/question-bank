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
@ApiModel(description = "Summary DTO for class information used in nested responses")
public class ClassSummaryDTO {

    @ApiModelProperty(
            value = "Class ID",
            example = "1"
    )
    private Long id;

    @ApiModelProperty(
            value = "Class name",
            example = "Grade 10"
    )
    private String name;

    @ApiModelProperty(
            value = "Class description",
            example = "Secondary school grade 10 students"
    )
    private String description;
}