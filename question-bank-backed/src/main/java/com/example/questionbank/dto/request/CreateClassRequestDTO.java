package com.example.questionbank.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Request DTO for creating a new class")
public class CreateClassRequestDTO {

    @ApiModelProperty(
            value = "Class name",
            example = "Grade 10",
            required = true
    )
    @NotBlank(message = "Class name is required")
    @Size(min = 1, max = 100, message = "Class name must be between 1 and 100 characters")
    private String name;

    @ApiModelProperty(
            value = "Class description",
            example = "Secondary school grade 10 students"
    )
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}