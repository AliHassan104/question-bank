package com.example.questionbank.dto.response;

import com.example.questionbank.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Response DTO for class information")
public class ClassResponseDTO extends BaseDTO {

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

    @ApiModelProperty(
            value = "Active status",
            example = "true"
    )
    private Boolean isActive;

    @ApiModelProperty(
            value = "Number of subjects in this class",
            example = "5"
    )
    private Integer subjectCount;
}