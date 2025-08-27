package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserUpdate;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findByAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable @PositiveOrZero long id) {
        return userClient.findById(id);
    }

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody User newUser) {
        return userClient.save(newUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Valid @RequestBody UserUpdate updateUser, @PathVariable long id) {
        return userClient.update(updateUser, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable @PositiveOrZero long id) {
        return userClient.delete(id);
    }
}
