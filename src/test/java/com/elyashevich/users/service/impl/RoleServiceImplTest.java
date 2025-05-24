package com.elyashevich.users.service.impl;

import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.exception.ResourceAlreadyExistsException;
import com.elyashevich.users.exception.ResourceNotFoundException;
import com.elyashevich.users.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Role Service Implementation Tests")
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "MODERATOR"})
    @DisplayName("create_WithValidName_ReturnsCreatedRole")
    void createRole_WithValidName_ReturnsCreatedRole(String roleName) {
        // Arrange
        when(roleRepository.existsByName(roleName)).thenReturn(false);
        Role expectedRole = Role.builder().name(roleName).build();
        when(roleRepository.save(any(Role.class))).thenReturn(expectedRole);

        // Act
        Role result = roleService.create(roleName);

        // Assert
        assertAll(
                () -> assertEquals(roleName, result.getName()),
                () -> verify(roleRepository).existsByName(roleName),
                () -> verify(roleRepository).save(any(Role.class))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "MODERATOR"})
    @DisplayName("create_WithExistingName_ThrowsResourceAlreadyExistsException")
    void createRole_WithExistingName_ThrowsException(String roleName) {
        // Arrange
        when(roleRepository.existsByName(roleName)).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> roleService.create(roleName)
        );

        assertEquals(
                String.format(RoleServiceImpl.ROLE_WITH_NAME_ALREADY_EXISTS_TEMPLATE, roleName),
                exception.getMessage()
        );
        verify(roleRepository).existsByName(roleName);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "MODERATOR"})
    @DisplayName("findByName_WithExistingName_ReturnsRole")
    void findByName_WithExistingName_ReturnsRole(String roleName) {
        // Arrange
        Role expectedRole = Role.builder().name(roleName).build();
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(expectedRole));

        // Act
        Role result = roleService.findByName(roleName);

        // Assert
        assertAll(
                () -> assertEquals(roleName, result.getName()),
                () -> verify(roleRepository).findByName(roleName)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "MODERATOR"})
    @DisplayName("findByName_WithNonExistingName_ThrowsResourceNotFoundException")
    void findByName_WithNonExistingName_ThrowsException(String roleName) {
        // Arrange
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByName(roleName)
        );

        assertEquals(
                String.format(RoleServiceImpl.ROLE_WITH_NAME_WAS_NOT_FOUND_TEMPLATE, roleName),
                exception.getMessage()
        );
        verify(roleRepository).findByName(roleName);
    }

    @Test
    @DisplayName("update_WithValidNames_ReturnsUpdatedRole")
    void updateRole_WithValidNames_ReturnsUpdatedRole() {
        // Arrange
        final String oldName = "OLD_ROLE";
        final String newName = "NEW_ROLE";

        Role existingRole = Role.builder().name(oldName).build();
        Role updatedRole = Role.builder().name(newName).build();

        when(roleRepository.findByName(oldName)).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByName(newName)).thenReturn(false);
        when(roleRepository.save(existingRole)).thenReturn(updatedRole);

        // Act
        Role result = roleService.update(oldName, newName);

        // Assert
        assertAll(
                () -> assertEquals(newName, result.getName()),
                () -> verify(roleRepository).findByName(oldName),
                () -> verify(roleRepository).existsByName(newName),
                () -> verify(roleRepository).save(existingRole)
        );
    }

    @Test
    @DisplayName("update_WithNonExistingOldName_ThrowsResourceNotFoundException")
    void updateRole_WithNonExistingOldName_ThrowsException() {
        // Arrange
        final String oldName = "NON_EXISTENT";
        final String newName = "NEW_NAME";

        when(roleRepository.findByName(oldName)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.update(oldName, newName)
        );

        assertEquals(
                String.format(RoleServiceImpl.ROLE_WITH_NAME_WAS_NOT_FOUND_TEMPLATE, oldName),
                exception.getMessage()
        );
        verify(roleRepository).findByName(oldName);
        verify(roleRepository, never()).existsByName(any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("update_WithExistingNewName_ThrowsResourceAlreadyExistsException")
    void updateRole_WithExistingNewName_ThrowsException() {
        // Arrange
        final String oldName = "OLD_ROLE";
        final String newName = "EXISTING_ROLE";

        Role existingRole = Role.builder().name(oldName).build();
        when(roleRepository.findByName(oldName)).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByName(newName)).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> roleService.update(oldName, newName)
        );

        assertEquals(
                String.format(RoleServiceImpl.ROLE_WITH_NAME_ALREADY_EXISTS_TEMPLATE, newName),
                exception.getMessage()
        );
        verify(roleRepository).findByName(oldName);
        verify(roleRepository).existsByName(newName);
        verify(roleRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete_WithExistingName_DeletesRole")
    void deleteRole_WithExistingName_DeletesRole() {
        // Arrange
        final String roleName = "ROLE_TO_DELETE";
        Role role = Role.builder().name(roleName).build();

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(role);

        // Act
        roleService.delete(roleName);

        // Assert
        verify(roleRepository).findByName(roleName);
        verify(roleRepository).delete(role);
    }

    @Test
    @DisplayName("delete_WithNonExistingName_ThrowsResourceNotFoundException")
    void deleteRole_WithNonExistingName_ThrowsException() {
        // Arrange
        final String roleName = "NON_EXISTENT";

        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.delete(roleName)
        );

        assertEquals(
                String.format(RoleServiceImpl.ROLE_WITH_NAME_WAS_NOT_FOUND_TEMPLATE, roleName),
                exception.getMessage()
        );
        verify(roleRepository).findByName(roleName);
        verify(roleRepository, never()).delete(any());
    }
}