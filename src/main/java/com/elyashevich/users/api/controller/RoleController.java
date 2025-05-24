package com.elyashevich.users.api.controller;

import com.elyashevich.users.api.dto.RoleCreateDto;
import com.elyashevich.users.domain.entity.Role;
import com.elyashevich.users.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{name}")
    public Role findByName(@PathVariable("name") String name) {
        return this.roleService.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Role create(@Valid @RequestBody RoleCreateDto dto) {
        return this.roleService.create(dto.name());
    }

    @PatchMapping("/{oldName}")
    @ResponseStatus(HttpStatus.CREATED)
    public Role update(@PathVariable("oldName") String oldName, @Valid @RequestBody RoleCreateDto dto) {
        return this.roleService.update(oldName, dto.name());
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("name") String name) {
        this.roleService.delete(name);
    }
}
