package com.elyashevich.users.service;

import com.elyashevich.users.domain.entity.User;

public interface UserService {

    User save(User user);

    User findByEmail(String email);
}
