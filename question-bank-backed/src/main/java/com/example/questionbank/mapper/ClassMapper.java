package com.example.questionbank.mapper;

import com.example.questionbank.dto.request.CreateClassRequestDTO;
import com.example.questionbank.dto.request.UpdateClassRequestDTO;
import com.example.questionbank.dto.response.ClassResponseDTO;
import com.example.questionbank.dto.response.ClassSummaryDTO;
import com.example.questionbank.model.ClassEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassMapper {

    // Convert CreateClassRequestDTO to Entity
    @Mapping(target = "subjects", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    ClassEntity toEntity(CreateClassRequestDTO dto);

    // Convert Entity to ClassResponseDTO
    @Mapping(target = "subjectCount", expression = "java(entity.getSubjectCount())")
    ClassResponseDTO toResponseDTO(ClassEntity entity);

    // Convert Entity to ClassSummaryDTO
    ClassSummaryDTO toSummaryDTO(ClassEntity entity);

    // Convert List of Entities to List of ResponseDTOs
    List<ClassResponseDTO> toResponseDTOList(List<ClassEntity> entities);

    // Convert List of Entities to List of SummaryDTOs
    List<ClassSummaryDTO> toSummaryDTOList(List<ClassEntity> entities);

    // Update entity from UpdateClassRequestDTO
    @Mapping(target = "subjects", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UpdateClassRequestDTO dto, @MappingTarget ClassEntity entity);

}