package com.elyashevich.users.api.dto;

import com.elyashevich.users.domain.entity.Role;

import java.util.Set;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<Role> roles
) {
}
