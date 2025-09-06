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
@ApiModel(description = "Request DTO for creating a new subject")
public class CreateSubjectRequestDTO {

    @ApiModelProperty(value = "Subject name", required = true)
    @NotBlank(message = "Subject name is required")
    @Size(min = 1, max = 100)
    private String name;

    @ApiModelProperty(value = "Subject description")
    @Size(max = 500)
    private String description;

    @ApiModelProperty(value = "Class ID", required = true)
    @NotNull(message = "Class ID is required")
    private Long classId;
}