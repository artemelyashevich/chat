package com.elyashevich.users.api.dto;

import jakarta.validation.constraints.NotNull;

public record RoleCreateDto(
        @NotNull(message = "Name must be not empty")
        String name
) {
}
