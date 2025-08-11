package ru.practicum.shareit.user.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
public class UserMapperDto {
    public static UserDto toDto(User user) {
        log.trace("UserMapperDto.toDto: {}", user.getName());
        return UserDto
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
