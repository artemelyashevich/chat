package com.elyashevich.users.service.impl;

import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.exception.ResourceAlreadyExistsException;
import com.elyashevich.users.exception.ResourceNotFoundException;
import com.elyashevich.users.repository.RoleRepository;
import com.elyashevich.users.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    public static final String ROLE_WITH_NAME_ALREADY_EXISTS_TEMPLATE = "Role with name: '%s' already exists";
    public static final String ROLE_WITH_NAME_WAS_NOT_FOUND_TEMPLATE = "Role with name: '%s' was not found.";

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public Role create(String name) {
        log.debug("Attempting create new role with name: {}", name);

        this.checkIfRoleAlreadyExistsByName(name);

        Role role = this.roleRepository.save(Role.builder()
                        .name(name)
                .build());

        log.info("Role created: {}", role);
        return role;
    }

    @Override
    public Role findByName(String name) {
        log.debug("Attempting find role by name: {}", name);

        Role role = this.roleRepository.findByName(name).orElseThrow(
                () -> {
                    String message = ROLE_WITH_NAME_WAS_NOT_FOUND_TEMPLATE.formatted(name);
                    log.info(message);
                    return new ResourceNotFoundException(message);
                }
        );

        log.info("Role found by name = '{}' : {}", name, role);
        return role;
    }

    @Override
    @Transactional
    public Role update(String oldName, String newName) {
        log.debug("Attempting update role with name: '{}' to name: {}", oldName, newName);

        Role role = this.findByName(oldName);

        this.checkIfRoleAlreadyExistsByName(newName);

        role.setName(newName);
        Role updated = this.roleRepository.save(role);

        log.info("Role updated: {}", updated);
        return updated;
    }

    @Override
    @Transactional
    public void delete(String name) {
        log.debug("Attempting delete role by name: {}", name);
        Role role = this.findByName(name);

        this.roleRepository.delete(role);

        log.info("Role with name: {} deleted", name);
    }

    private void checkIfRoleAlreadyExistsByName(String newName) {
        if (this.roleRepository.existsByName(newName)) {
            String message = ROLE_WITH_NAME_ALREADY_EXISTS_TEMPLATE.formatted(newName);
            log.info(message);
            throw new ResourceAlreadyExistsException(message);
        }
    }
}
