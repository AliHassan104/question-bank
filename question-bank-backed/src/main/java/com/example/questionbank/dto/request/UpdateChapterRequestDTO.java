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
@ApiModel(description = "Request DTO for updating an existing chapter")
public class UpdateChapterRequestDTO {

    @ApiModelProperty(value = "Chapter name", required = true)
    @NotBlank(message = "Chapter name is required")
    @Size(min = 1, max = 100)
    private String name;

    @ApiModelProperty(value = "Chapter description")
    @Size(max = 500)
    private String description;

    @ApiModelProperty(value = "Subject ID", required = true)
    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @ApiModelProperty(value = "Active status")
    private Boolean isActive;
}
