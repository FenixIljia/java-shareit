package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    public Optional<User> findById(long id);

    public Optional<User> findByEmail(String email);

    public Optional<List<User>> findByName(String name);
}
