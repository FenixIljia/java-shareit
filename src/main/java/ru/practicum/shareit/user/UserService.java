package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;

import java.util.List;

public interface UserService {

    public List<UserDto> findAll();

    public UserDto findById(long id);

    public UserDto findByEmail(String email);

    public List<UserDto> findByName(String name);

    public User save(User user);

    public User update(UserUpdate user, long userId);

    public void delete(long id);
}
