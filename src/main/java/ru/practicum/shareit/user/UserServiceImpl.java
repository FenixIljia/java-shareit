package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;
import ru.practicum.shareit.user.mapper.UserMapperDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean delete(long id) {
        log.info("UserServiceImpl.delete");
        return userRepository.delete(id);
    }

    @Override
    public List<UserDto> findAll() {
        log.info("UserServiceImpl.findAll");
        return userRepository.findAll()
                .stream()
                .map(UserMapperDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findByEmail(String email) {
        log.info("UserServiceImpl.findByEmail");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return UserMapperDto.toDto(user.get());
        }
        log.warn("User with email {} not found", email);
        throw new NotFoundException("User with email " + email + " not found");
    }

    @Override
    public UserDto findById(long id) {
        log.info("UserServiceImpl.findById");
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapperDto.toDto(user.get());
        }
        log.warn("User with id {} not found", id);
        throw new NotFoundException("User with id " + id + " not found");
    }

    @Override
    public List<UserDto> findByName(String name) {
        log.info("UserServiceImpl.findByName");
        Optional<List<User>> users = userRepository.findByName(name);
        if (users.isPresent()) {
            return users.get()
                    .stream()
                    .map(UserMapperDto::toDto)
                    .collect(Collectors.toList());
        }
        log.warn("User with name {} not found", name);
        throw new NotFoundException("User with name " + name + " not found");
    }

    @Override
    public User save(User user) {
        log.info("UserServiceImpl.save");
        checkDuplicationEmail(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User update(UserUpdate user, long userId) {
        log.info("UserServiceImpl.update");
        if (userRepository.findById(userId).isEmpty()) {
            log.warn("User with id: {} not found", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        }
        checkDuplicationEmail(user.getEmail());
        return userRepository.update(user,  userId);
    }

    private boolean checkDuplicationEmail(String email) {
        log.info("UserServiceImpl.checkDuplication {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new DuplicatedDataException("User with email " + email + " already exists");
        }
        return true;
    }
}
