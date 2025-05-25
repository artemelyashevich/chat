package com.elyashevich.users.api.dto;

import com.elyashevich.users.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;


@Schema(description = "User response data")
public record UserResponseDto(
        @Schema(description = "Unique identifier of the user", example = "1")
        Long id,

        @Schema(description = "First name of the user", example = "John")
        String firstName,

        @Schema(description = "Last name of the user", example = "Doe")
        String lastName,

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,

        @Schema(description = "Roles assigned to the user")
        Set<Role> roles
) {
}
