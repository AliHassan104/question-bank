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
@ApiModel(description = "Response DTO for MCQ option information")
public class MCQOptionResponseDTO extends BaseDTO {

    @ApiModelProperty(value = "Option text")
    private String optionText;

    @ApiModelProperty(value = "Is correct option")
    private Boolean isCorrect;

    @ApiModelProperty(value = "Option order")
    private Integer optionOrder;

    @ApiModelProperty(value = "Option image URL")
    private String optionImageUrl;
}