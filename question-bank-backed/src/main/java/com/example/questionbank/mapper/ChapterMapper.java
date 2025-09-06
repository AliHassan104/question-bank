package com.example.questionbank.mapper;

import com.example.questionbank.dto.request.CreateChapterRequestDTO;
import com.example.questionbank.dto.request.UpdateChapterRequestDTO;
import com.example.questionbank.dto.response.ChapterResponseDTO;
import com.example.questionbank.model.Chapter;
import com.example.questionbank.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface ChapterMapper {

    @Mapping(target = "subject", source = "subjectId")
    Chapter toEntity(CreateChapterRequestDTO dto);

    @Mapping(target = "subjectInfo", source = "subject")
    @Mapping(target = "questionCount", expression = "java(entity.getQuestionCount())")
    ChapterResponseDTO toResponseDTO(Chapter entity);

    List<ChapterResponseDTO> toResponseDTOList(List<Chapter> entities);

    @Mapping(target = "subject", source = "subjectId")
    void updateEntityFromDTO(UpdateChapterRequestDTO dto, @MappingTarget Chapter entity);

    // Custom mapping method to convert Long subjectId to Subject
    default Subject mapSubjectId(Long subjectId) {
        if (subjectId == null) {
            return null;
        }
        Subject subject = new Subject();
        subject.setId(subjectId);
        return subject;
    }
}