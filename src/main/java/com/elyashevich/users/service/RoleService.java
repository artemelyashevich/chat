package com.elyashevich.users.service;

import com.elyashevich.users.domain.entity.Role;

public interface RoleService {

    Role create(String name);

    Role update(String oldName, String newName);

    Role findByName(String name);

    void delete(String name);
}
