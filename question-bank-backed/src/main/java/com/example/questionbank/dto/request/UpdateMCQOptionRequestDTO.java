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
@ApiModel(description = "Request DTO for updating MCQ option")
public class UpdateMCQOptionRequestDTO {

    @ApiModelProperty(value = "Option ID (for existing options)")
    private Long id;

    @ApiModelProperty(value = "Option text", required = true)
    @NotBlank(message = "Option text is required")
    @Size(min = 1, max = 1000, message = "Option text must be between 1 and 1000 characters")
    private String optionText;

    @ApiModelProperty(value = "Is this option correct?")
    private Boolean isCorrect;

    @ApiModelProperty(value = "Option order/sequence")
    private Integer optionOrder;

    @ApiModelProperty(value = "Option image URL")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String optionImageUrl;
}