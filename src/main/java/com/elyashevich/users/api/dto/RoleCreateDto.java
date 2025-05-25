package com.elyashevich.users.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoleCreateDto(
        @NotNull(message = "Name must not be null")
        @NotBlank(message = "Name must not be blank")
        @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters")
        String name
) {
}