package com.elyashevich.users.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Schema(description = "User request data for creation")
public record UserRequestDto(
        @NotBlank(message = "First name must not be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Schema(
                description = "First name of the user",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 50,
                example = "John"
        )
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Schema(
                description = "Last name of the user",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 50,
                example = "Doe"
        )
        String lastName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        @Schema(
                description = "Email address of the user",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "john.doe@example.com"
        )
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
        )
        @Schema(
                description = "Password for the user account",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8,
                maxLength = 100,
                example = "P@ssw0rd123",
                format = "password"
        )
        String password
) {
}