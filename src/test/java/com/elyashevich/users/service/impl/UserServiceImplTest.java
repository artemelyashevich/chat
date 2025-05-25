package com.elyashevich.users.service.impl;

import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.domain.entity.User;
import com.elyashevich.users.exception.ResourceAlreadyExistsException;
import com.elyashevich.users.exception.ResourceNotFoundException;
import com.elyashevich.users.repository.UserRepository;
import com.elyashevich.users.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service Implementation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @Order(1)
    @DisplayName("save_WithNewUser_ReturnsSavedUser")
    void save_WithNewUser_ReturnsSavedUser() {
        // Arrange
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .password("password")
                .build();
        
        Role userRole = Role.builder().name(UserServiceImpl.ROLE_USER).build();
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleService.findByName(UserServiceImpl.ROLE_USER)).thenReturn(userRole);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.save(user);

        // Assert
        assertAll(
            () -> assertEquals(email, result.getEmail()),
            () -> assertTrue(result.getRoles().contains(userRole)),
            () -> verify(userRepository).existsByEmail(email),
            () -> verify(roleService).findByName(UserServiceImpl.ROLE_USER),
            () -> verify(userRepository).save(user)
        );
    }

    @Test
    @Order(2)
    @DisplayName("save_WithExistingEmail_ThrowsResourceAlreadyExistsException")
    void save_WithExistingEmail_ThrowsException() {
        // Arrange
        String email = "existing@example.com";
        User user = User.builder().email(email).build();
        
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
            ResourceAlreadyExistsException.class,
            () -> userService.save(user)
        );

        assertEquals(
            String.format("User with email: '%s' already exists", email),
            exception.getMessage()
        );
        verify(userRepository).existsByEmail(email);
        verify(roleService, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @Order(3)
    @DisplayName("findByEmail_WithExistingEmail_ReturnsUser")
    void findByEmail_WithExistingEmail_ReturnsUser() {
        // Arrange
        String email = "found@example.com";
        User expectedUser = User.builder().email(email).build();
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertEquals(expectedUser, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @Order(4)
    @DisplayName("findByEmail_WithNonExistingEmail_ThrowsResourceNotFoundException")
    void findByEmail_WithNonExistingEmail_ThrowsException() {
        // Arrange
        String email = "notfound@example.com";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.findByEmail(email)
        );

        assertEquals(
            String.format(UserServiceImpl.USER_WITH_EMAIL_WAS_NOT_FOUND_TEMPLATE, email),
            exception.getMessage()
        );
        verify(userRepository).findByEmail(email);
    }
}