package com.elyashevich.users.api.controller;

import com.elyashevich.users.api.dto.UserRequestDto;
import com.elyashevich.users.api.dto.UserResponseDto;
import com.elyashevich.users.api.mapper.UserMapper;
import com.elyashevich.users.domain.entity.User;
import com.elyashevich.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{email}")
    @Operation(
            summary = "Get user by email",
            description = "Retrieves a user by their email address",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    public UserResponseDto findByEmail(
            @Parameter(
                    description = "Email address of the user to be retrieved",
                    required = true,
                    example = "user@example.com"
            )
            @PathVariable String email) {
        User user = this.userService.findByEmail(email);
        return this.userMapper.toDto(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new user",
            description = "Registers a new user in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists"
                    )
            }
    )
    public UserResponseDto create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequestDto.class)))
            @Valid @RequestBody UserRequestDto dto) {
        User user = this.userService.save(this.userMapper.toEntity(dto));
        return this.userMapper.toDto(user);
    }
}
