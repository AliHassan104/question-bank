package com.example.questionbank.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Request DTO for updating an existing subject")
public class UpdateSubjectRequestDTO {

    @ApiModelProperty(
            value = "Subject name",
            example = "Advanced Mathematics",
            required = true
    )
    @NotBlank(message = "Subject name is required")
    @Size(min = 1, max = 100, message = "Subject name must be between 1 and 100 characters")
    private String name;

    @ApiModelProperty(
            value = "Subject description",
            example = "Advanced level mathematics for grade 12 students"
    )
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @ApiModelProperty(
            value = "Class ID",
            example = "1",
            required = true
    )
    @NotNull(message = "Class ID is required")
    private Long classId;

    @ApiModelProperty(
            value = "Active status",
            example = "true"
    )
    private Boolean isActive;
}
