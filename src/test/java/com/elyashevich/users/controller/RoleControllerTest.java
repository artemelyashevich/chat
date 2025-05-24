package com.elyashevich.users.controller;

import com.elyashevich.users.api.controller.RoleController;
import com.elyashevich.users.api.dto.RoleCreateDto;
import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.exception.ResourceAlreadyExistsException;
import com.elyashevich.users.exception.ResourceNotFoundException;
import com.elyashevich.users.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@DisplayName("Role Controller Tests")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    private static final String BASE_URL = "/api/v1/roles";

    private Role testRole;
    private RoleCreateDto validDto;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        validDto = new RoleCreateDto("ADMIN");
    }

    @Test
    @DisplayName("GET /{name} - Success")
    void findByName_WithExistingName_ReturnsRole() throws Exception {
        given(roleService.findByName("ADMIN")).willReturn(testRole);

        mockMvc.perform(get(BASE_URL + "/{name}", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService).findByName("ADMIN");
    }

    @Test
    @DisplayName("GET /{name} - Not Found")
    void findByName_WithNonExistingName_ReturnsNotFound() throws Exception {
        given(roleService.findByName("UNKNOWN"))
                .willThrow(new ResourceNotFoundException("Role not found"));

        mockMvc.perform(get(BASE_URL + "/{name}", "UNKNOWN"))
                .andExpect(status().isNotFound());

        verify(roleService).findByName("UNKNOWN");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("GET /{name} - Bad Request (Invalid Name)")
    void findByName_WithInvalidName_ReturnsBadRequest(String invalidName) throws Exception {
        mockMvc.perform(get(BASE_URL + "/{name}", invalidName))
                .andExpect(status().isMethodNotAllowed());

        verify(roleService, never()).findByName(any());
    }

    @Test
    @DisplayName("POST / - Success")
    void create_WithValidDto_ReturnsCreatedRole() throws Exception {
        given(roleService.create("ADMIN")).willReturn(testRole);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService).create("ADMIN");
    }

    @Test
    @DisplayName("POST / - Bad request (Existing Role)")
    void create_WithExistingRole_ReturnsConflict() throws Exception {
        given(roleService.create("ADMIN"))
                .willThrow(new ResourceAlreadyExistsException("Role already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest());

        verify(roleService).create("ADMIN");
    }

    @Test
    @DisplayName("POST / - Bad Request (Empty Body)")
    void create_WithEmptyBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).create(any());
    }

    @Test
    @DisplayName("POST / - Bad Request (Null Name)")
    void create_WithNullName_ReturnsBadRequest() throws Exception {
        RoleCreateDto invalidDto = new RoleCreateDto(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).create(any());
    }

    // PATCH /{oldName} Tests
    @Test
    @DisplayName("PATCH /{oldName} - Success")
    void update_WithValidInput_ReturnsUpdatedRole() throws Exception {
        given(roleService.update("OLD_ROLE", "NEW_ROLE")).willReturn(testRole);

        mockMvc.perform(patch(BASE_URL + "/{oldName}", "OLD_ROLE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoleCreateDto("NEW_ROLE"))))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"));

        verify(roleService).update("OLD_ROLE", "NEW_ROLE");
    }

    @Test
    @DisplayName("PATCH /{oldName} - Not Found")
    void update_WithNonExistingRole_ReturnsNotFound() throws Exception {
        given(roleService.update("OLD_ROLE", "NEW_ROLE"))
                .willThrow(new ResourceNotFoundException("Role not found"));

        mockMvc.perform(patch(BASE_URL + "/{oldName}", "OLD_ROLE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoleCreateDto("NEW_ROLE"))))
                .andExpect(status().isNotFound());

        verify(roleService).update("OLD_ROLE", "NEW_ROLE");
    }

    @Test
    @DisplayName("PATCH /{oldName} - Bad request")
    void update_WithExistingNewName_ReturnsConflict() throws Exception {
        given(roleService.update("OLD_ROLE", "EXISTING_ROLE"))
                .willThrow(new ResourceAlreadyExistsException("Role already exists"));

        mockMvc.perform(patch(BASE_URL + "/{oldName}", "OLD_ROLE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoleCreateDto("EXISTING_ROLE"))))
                .andExpect(status().isBadRequest());

        verify(roleService).update("OLD_ROLE", "EXISTING_ROLE");
    }

    @Test
    @DisplayName("DELETE /{name} - Success")
    void delete_WithExistingName_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{name}", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(roleService).delete("ADMIN");
    }

    @Test
    @DisplayName("DELETE /{name} - Not Found")
    void delete_WithNonExistingName_ReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Role not found"))
                .when(roleService).delete("UNKNOWN");

        mockMvc.perform(delete(BASE_URL + "/{name}", "UNKNOWN"))
                .andExpect(status().isNotFound());

        verify(roleService).delete("UNKNOWN");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("DELETE /{name} - Bad Request")
    void delete_WithInvalidName_ReturnsBadRequest(String invalidName) throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{name}", invalidName))
                .andExpect(status().isMethodNotAllowed());

        verify(roleService, never()).delete(any());
    }
}