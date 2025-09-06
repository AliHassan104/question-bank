package com.example.questionbank.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {

    @ApiModelProperty(value = "Entity ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "Creation timestamp", example = "2025-01-20T10:30:00")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "Last update timestamp", example = "2025-01-20T15:45:00")
    private LocalDateTime updatedAt;

    @ApiModelProperty(value = "Created by user", example = "admin")
    private String createdBy;

    @ApiModelProperty(value = "Last updated by user", example = "admin")
    private String updatedBy;
}