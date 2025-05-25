package com.elyashevich.users.api.controller;

import com.elyashevich.users.api.dto.UserRequestDto;
import com.elyashevich.users.api.dto.UserResponseDto;
import com.elyashevich.users.api.mapper.UserMapper;
import com.elyashevich.users.domain.entity.User;
import com.elyashevich.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{email}")
    public UserResponseDto findByEmail(@PathVariable String email) {
        User user = this.userService.findByEmail(email);
        return this.userMapper.toDto(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Valid @RequestBody UserRequestDto dto) {
        User user = this.userService.save(this.userMapper.toEntity(dto));
        return this.userMapper.toDto(user);
    }
}
