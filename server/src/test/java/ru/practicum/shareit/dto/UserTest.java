package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserTest {

    @Test
    public void userTest() {
        Long userId = 1L;
        String name = "John";
        String email = "email";

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);

        User user2 = new User();
        user2.setId(userId);
        user2.setName(name);
        user2.setEmail(email);
        assertThat(user.hashCode(), equalTo(user2.hashCode()));
        assertThat(user.equals(user2), equalTo(true));
    }
}
