package com.example.questionbank.mapper;

import com.example.questionbank.dto.request.CreateMCQOptionRequestDTO;
import com.example.questionbank.dto.request.UpdateMCQOptionRequestDTO;
import com.example.questionbank.dto.response.MCQOptionResponseDTO;
import com.example.questionbank.model.MCQOption;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MCQOptionMapper {

    // Convert CreateMCQOptionRequestDTO to Entity
    @Mapping(target = "question", ignore = true)
    MCQOption toEntity(CreateMCQOptionRequestDTO dto);

    // Convert Entity to MCQOptionResponseDTO
    MCQOptionResponseDTO toResponseDTO(MCQOption entity);

    // Convert List of Entities to List of ResponseDTOs
    List<MCQOptionResponseDTO> toResponseDTOList(List<MCQOption> entities);

    // Convert List of CreateDTOs to List of Entities
    List<MCQOption> toEntityList(List<CreateMCQOptionRequestDTO> dtos);

    // Update entity from UpdateMCQOptionRequestDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", ignore = true)
    void updateEntityFromDTO(UpdateMCQOptionRequestDTO dto, @MappingTarget MCQOption entity);

    // Convert UpdateMCQOptionRequestDTO to Entity (for new options during update)
    @Mapping(target = "question", ignore = true)
    MCQOption toEntityFromUpdateDTO(UpdateMCQOptionRequestDTO dto);

    // Convert List of UpdateDTOs to List of Entities
    List<MCQOption> toEntityListFromUpdateDTOs(List<UpdateMCQOptionRequestDTO> dtos);
}