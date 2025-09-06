package com.example.questionbank.mapper;

import com.example.questionbank.dto.request.CreateQuestionRequestDTO;
import com.example.questionbank.dto.request.UpdateQuestionRequestDTO;
import com.example.questionbank.dto.response.QuestionResponseDTO;
import com.example.questionbank.model.Question;
import com.example.questionbank.model.Chapter;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MCQOptionMapper.class, ChapterMapper.class})
public interface QuestionMapper {

    // Convert CreateQuestionRequestDTO to Entity
    @Mapping(target = "chapter", source = "chapterId")
    @Mapping(target = "isAddedToPaper", constant = "false")
    @Mapping(target = "transientOptions", ignore = true)
    Question toEntity(CreateQuestionRequestDTO dto);

    // Convert Entity to QuestionResponseDTO
    @Named("toResponseDTO")
    @Mapping(target = "chapterInfo", source = "chapter")
    QuestionResponseDTO toResponseDTO(Question entity);

    // Convert List of Entities to List of ResponseDTOs
    @IterableMapping(qualifiedByName = "toResponseDTO")
    List<QuestionResponseDTO> toResponseDTOList(List<Question> entities);

    // Update entity from UpdateQuestionRequestDTO
    @Mapping(target = "chapter", source = "chapterId")
    @Mapping(target = "transientOptions", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdateQuestionRequestDTO dto, @MappingTarget Question entity);

    // Convert Entity to QuestionResponseDTO without options (for performance)
    @Named("toResponseDTOWithoutOptions")
    @Mapping(target = "chapterInfo", source = "chapter")
    QuestionResponseDTO toResponseDTOWithoutOptions(Question entity);

    // Convert List of Entities to List of ResponseDTOs without options
    @IterableMapping(qualifiedByName = "toResponseDTOWithoutOptions")
    List<QuestionResponseDTO> toResponseDTOListWithoutOptions(List<Question> entities);

    // Custom mapping method to convert Long chapterId to Chapter
    default Chapter mapChapterId(Long chapterId) {
        if (chapterId == null) {
            return null;
        }
        Chapter chapter = new Chapter();
        chapter.setId(chapterId);
        return chapter;
    }

    // Custom mapping for handling default values
    @AfterMapping
    default void setDefaultValues(@MappingTarget Question entity) {
        if (entity.isAddedToPaper() == false) {
            entity.setIsAddedToPaper(false);
        }
    }
}