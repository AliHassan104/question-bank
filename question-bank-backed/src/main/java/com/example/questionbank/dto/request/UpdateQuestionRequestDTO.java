package com.example.questionbank.dto.request;

import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.DifficultyLevel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Request DTO for updating an existing question")
public class UpdateQuestionRequestDTO {
    @ApiModelProperty(value = "Question ID (for existing questions)")
    private Long id;

    @ApiModelProperty(value = "Question text", required = true)
    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 5000, message = "Question text must be between 10 and 5000 characters")
    private String questionText;

    @ApiModelProperty(value = "Question image URL")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String questionImageUrl;

    @ApiModelProperty(value = "Explanation for the question")
    @Size(max = 2000, message = "Explanation must not exceed 2000 characters")
    private String explanation;

    @ApiModelProperty(value = "Section type", required = true)
    @NotNull(message = "Section type is required")
    private SectionType sectionType;

    @ApiModelProperty(value = "Question type", required = true)
    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @ApiModelProperty(value = "Difficulty level")
    private DifficultyLevel difficultyLevel;

    @ApiModelProperty(value = "Marks for correct answer", required = true)
    @NotNull(message = "Marks are required")
    @DecimalMin(value = "0.1", message = "Marks must be at least 0.1")
    @DecimalMax(value = "100.0", message = "Marks must not exceed 100")
    private Double marks;

    @ApiModelProperty(value = "Negative marks for wrong answer")
    @DecimalMin(value = "0.0", message = "Negative marks cannot be negative")
    @DecimalMax(value = "10.0", message = "Negative marks must not exceed 10")
    private Double negativeMarks;

    @ApiModelProperty(value = "Time limit in seconds")
    @Min(value = 1, message = "Time limit must be at least 1 second")
    @Max(value = 3600, message = "Time limit must not exceed 1 hour")
    private Integer timeLimitSeconds;

    @ApiModelProperty(value = "Chapter ID", required = true)
    @NotNull(message = "Chapter ID is required")
    private Long chapterId;

    @ApiModelProperty(value = "Active status")
    private Boolean isActive;

    @ApiModelProperty(value = "Added to paper status")
    private Boolean isAddedToPaper;

    @ApiModelProperty(value = "MCQ options (required for MCQ questions)")
    @Valid
    private List<UpdateMCQOptionRequestDTO> mcqOptions;
}