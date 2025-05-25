package com.elyashevich.users.service.impl;

import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.domain.entity.User;
import com.elyashevich.users.exception.ResourceAlreadyExistsException;
import com.elyashevich.users.exception.ResourceNotFoundException;
import com.elyashevich.users.repository.UserRepository;
import com.elyashevich.users.service.RoleService;
import com.elyashevich.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String USER_WITH_EMAIL_WAS_NOT_FOUND_TEMPLATE = "User with email: '%s' was not found";
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "UserServiceImpl::findByEmail", key = "#user.email"),
            }
    )
    public User save(User user) {
        log.debug("Attempting create new user: {}", user);

        if (this.userRepository.existsByEmail(user.getEmail())) {
            String message = "User with email: '%s' already exists".formatted(user.getEmail());
            log.info(message);
            throw new ResourceAlreadyExistsException(message);
        }

        Role role = this.roleService.findByName(ROLE_USER);

        user.setRoles(Set.of(role));

        User newUser = this.userRepository.save(user);

        log.info("User created: {}", newUser);
        return newUser;
    }

    @Override
    @Cacheable(value = "RoleServiceImpl::findByEmail", key = "#email")
    public User findByEmail(String email) {
        log.debug("Attempting fin user by email: '{}'", email);

        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> {
                    String message = USER_WITH_EMAIL_WAS_NOT_FOUND_TEMPLATE.formatted(email);
                    log.info(message);
                    return new ResourceNotFoundException(message);
                }
        );

        log.info("User found: {}", user);
        return user;
    }
}
