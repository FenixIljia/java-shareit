package ru.practicum.shareit.user.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapperDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryMemory implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private long id = 0L;

    @Override
    public User save(User user) {
        user.setId(generateId());
        users.add(user);
        return user;
    }

    @Override
    public boolean delete(long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    @Override
    public User update(UserUpdate user, long userId) {
        User oldUser = findById(userId).get();
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    @Override
    public Optional<User> findById(long id) {
        return users
                .stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<List<User>> findByName(String name) {
        return Optional.of(users
                .stream()
                .filter(user -> user.getName().equals(name))
                .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    private long generateId() {
        return id++;
    }
}