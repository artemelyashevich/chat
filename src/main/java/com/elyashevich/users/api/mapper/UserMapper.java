package com.elyashevich.users.api.mapper;

import com.elyashevich.users.api.dto.UserRequestDto;
import com.elyashevich.users.api.dto.UserResponseDto;
import com.elyashevich.users.domain.entity.User;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(UserRequestDto dto);
}
