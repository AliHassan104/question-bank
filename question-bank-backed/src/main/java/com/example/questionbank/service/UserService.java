package com.example.questionbank.service;

import com.example.questionbank.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserDto registerUser(UserDto userdto);
    List<UserDto> getAll();
    UserDto findById(Long id);
}
