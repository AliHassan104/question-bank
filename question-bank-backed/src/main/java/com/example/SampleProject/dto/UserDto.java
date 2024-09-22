package com.example.SampleProject.dto;

import com.example.SampleProject.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String name;
    private String password;
    private Set<Role> roles = new HashSet<>();
}
