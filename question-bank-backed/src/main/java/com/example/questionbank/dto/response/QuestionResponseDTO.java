package com.example.questionbank.dto.response;

import com.example.questionbank.dto.BaseDTO;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Response DTO for question information")
public class QuestionResponseDTO extends BaseDTO {

    @ApiModelProperty(value = "Question text")
    private String questionText;

    @ApiModelProperty(value = "Question image URL")
    private String questionImageUrl;

    @ApiModelProperty(value = "Explanation")
    private String explanation;

    @ApiModelProperty(value = "Section type")
    private SectionType sectionType;

    @ApiModelProperty(value = "Question type")
    private QuestionType questionType;

    @ApiModelProperty(value = "Difficulty level")
    private DifficultyLevel difficultyLevel;

    @ApiModelProperty(value = "Marks")
    private Double marks;

    @ApiModelProperty(value = "Negative marks")
    private Double negativeMarks;

    @ApiModelProperty(value = "Time limit in seconds")
    private Integer timeLimitSeconds;

    @ApiModelProperty(value = "Added to paper status")
    private Boolean isAddedToPaper;

    @ApiModelProperty(value = "Active status")
    private Boolean isActive;

    @ApiModelProperty(value = "Chapter information")
    private ChapterSummaryDTO chapterInfo;

    @ApiModelProperty(value = "MCQ options")
    private List<MCQOptionResponseDTO> mcqOptions;

    @ApiModelProperty(value = "Number of options")
    private Integer optionCount;
}