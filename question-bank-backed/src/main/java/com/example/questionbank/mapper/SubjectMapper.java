package com.example.questionbank.mapper;

import com.example.questionbank.dto.request.CreateSubjectRequestDTO;
import com.example.questionbank.dto.request.UpdateSubjectRequestDTO;
import com.example.questionbank.dto.response.SubjectResponseDTO;
import com.example.questionbank.dto.response.SubjectSummaryDTO;
import com.example.questionbank.model.ClassEntity;
import com.example.questionbank.model.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ClassMapper.class})
public interface SubjectMapper {

    @Mapping(target = "classEntity", source = "classId")
    Subject toEntity(CreateSubjectRequestDTO dto);

    @Mapping(target = "classInfo", source = "classEntity")
    SubjectResponseDTO toResponseDTO(Subject entity);

    List<SubjectResponseDTO> toResponseDTOList(List<Subject> entities);

    @Mapping(target = "classInfo", source = "classEntity")
    SubjectSummaryDTO toSummaryDTO(Subject entity);

    List<SubjectSummaryDTO> toSummaryDTOList(List<Subject> entities);

    // The update method
    @Mapping(target = "classEntity", source = "classId")
    void updateEntityFromDTO(UpdateSubjectRequestDTO dto, @MappingTarget Subject entity);

    // Custom mapping method to convert Long classId to ClassEntity
    default ClassEntity mapClassId(Long classId) {
        if (classId == null) {
            return null;
        }
        ClassEntity classEntity = new ClassEntity();
        classEntity.setId(classId);
        return classEntity;
    }
}