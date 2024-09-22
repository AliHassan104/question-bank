package com.example.SampleProject.service.impl;

import com.example.SampleProject.dto.UserDto;
import com.example.SampleProject.exception.RecordNotFoundException;
import com.example.SampleProject.model.Role;
import com.example.SampleProject.model.User;
import com.example.SampleProject.repository.RoleRepository;
import com.example.SampleProject.repository.UserRepository;
import com.example.SampleProject.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto registerUser(UserDto userdto) {
        User user = toEntity(userdto);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Set<Role> roleList = new HashSet<>();
        for(Role role: user.getRoles()){
            roleRepository.findById(role.getId())
                    .orElseThrow(()-> new RecordNotFoundException("Role not found"));
            roleList.add(role);
        }
        user.setRoles(roleList);
        userRepository.save(user);
        return toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for (User user : userList) {
            UserDto userDto = toDto(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return toDto(user);
        } else {
            throw new RecordNotFoundException(String.format("User not found for id => %d", id));
        }
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .password(userDto.getPassword())
                .roles(userDto.getRoles())
                .build();
    }
}
