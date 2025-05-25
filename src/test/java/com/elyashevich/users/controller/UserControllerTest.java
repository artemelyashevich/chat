package com.elyashevich.users.controller;

import com.elyashevich.users.api.controller.UserController;
import com.elyashevich.users.api.dto.UserRequestDto;
import com.elyashevich.users.api.dto.UserResponseDto;
import com.elyashevich.users.api.mapper.UserMapper;
import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.domain.entity.User;
import com.elyashevich.users.exception.ResourceNotFoundException;
import com.elyashevich.users.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private User testUser;
    private UserRequestDto validRequestDto;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        validRequestDto = new UserRequestDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "SecurePass123!"
        );

        responseDto = new UserResponseDto(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                Set.of(Role.builder().name("ROLE_USER").build())
        );
    }

    @Test
    @Order(1)
    @DisplayName("GET /{email} - Success")
    void findByEmail_WithValidEmail_ReturnsUser() throws Exception {
        // Given
        given(userService.findByEmail("john.doe@example.com")).willReturn(testUser);
        given(userMapper.toDto(testUser)).willReturn(responseDto);

        // When/Then
        mockMvc.perform(get("/api/v1/users/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userService).findByEmail("john.doe@example.com");
        verify(userMapper).toDto(testUser);
    }

    @Test
    @Order(2)
    @DisplayName("GET /{email} - Not Found")
    void findByEmail_WithNonExistingEmail_ReturnsNotFound() throws Exception {
        // Given
        given(userService.findByEmail("unknown@example.com"))
                .willThrow(new ResourceNotFoundException("User not found"));

        // When/Then
        mockMvc.perform(get("/api/v1/users/{email}", "unknown@example.com"))
                .andExpect(status().isNotFound());

        verify(userService).findByEmail("unknown@example.com");
    }

    @ParameterizedTest
    @Order(3)
    @NullAndEmptySource
    @DisplayName("GET /{email} - Bad Request (Invalid Email)")
    void findByEmail_WithInvalidEmail_ReturnsBadRequest(String invalidEmail) throws Exception {
        mockMvc.perform(get("/api/v1/users/{email}", invalidEmail))
                .andExpect(status().isMethodNotAllowed());

        verify(userService, never()).findByEmail(any());
    }

    @Test
    @Order(4)
    @DisplayName("POST / - Bad Request (Empty Body)")
    void create_WithEmptyBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    @ParameterizedTest
    @Order(5)
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("POST / - Bad Request (Blank First Name)")
    void create_WithBlankFirstName_ReturnsBadRequest(String invalidName) throws Exception {
        UserRequestDto invalidDto = new UserRequestDto(
                invalidName,
                "Doe",
                "john.doe@example.com",
                "SecurePass123!"
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    @Test
    @Order(6)
    @DisplayName("POST / - Bad Request (Invalid Email)")
    void create_WithInvalidEmail_ReturnsBadRequest() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto(
                "John",
                "Doe",
                "invalid-email",
                "SecurePass123!"
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }

    @Test
    @Order(7)
    @DisplayName("POST / - Bad Request (Weak Password)")
    void create_WithWeakPassword_ReturnsBadRequest() throws Exception {
        UserRequestDto invalidDto = new UserRequestDto(
                "John",
                "Doe",
                "john.doe@example.com",
                "weak"
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).save(any());
    }
}