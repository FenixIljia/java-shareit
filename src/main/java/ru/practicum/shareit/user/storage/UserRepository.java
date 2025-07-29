package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserUpdate;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    public List<User> findAll();

    public Optional<User> findById(long id);

    public Optional<User> findByEmail(String email);

    public Optional<List<User>> findByName(String name);

    public User save(User user);

    public User update(UserUpdate user, long userId);

    public boolean delete(long id);
}
