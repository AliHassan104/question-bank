package com.example.SampleProject.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginCredentials {
    private String name;
    private String password;
}
