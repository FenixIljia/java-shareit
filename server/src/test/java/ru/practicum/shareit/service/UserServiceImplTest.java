package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {

    private final UserService userService;
    private final EntityManager em;
    private final BaseServiceTest baseServiceTest;

    @Test
    public void findAllTest() {
        User user1 = baseServiceTest.createUser("test", "test");
        User user2 = baseServiceTest.createUser("test2", "test2");
        User user3 = baseServiceTest.createUser("test3", "test3");
        User user4 = baseServiceTest.createUser("test4", "test4");
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        userService.save(user4);
        List<UserDto> users = userService.findAll();
        assertThat(users.size(), equalTo(4));
    }

    @Test
    public void findByIdTest() {
        User user = baseServiceTest.createUser("test", "test");
        userService.save(user);
        UserDto userDto = userService.findById(user.getId());
        assertThat(userDto, notNullValue());
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
    }

    @Test
    public void findByEmailTest() {
        User user = baseServiceTest.createUser("test", "test");
        userService.save(user);
        UserDto userDto = userService.findByEmail(user.getEmail());
        assertThat(userDto, notNullValue());
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
    }

    @Test
    public void findByNameTest() {
        User user1 = baseServiceTest.createUser("test1", "test1");
        User user2 = baseServiceTest.createUser("test2", "test2");
        User user3 = baseServiceTest.createUser("test3", "test3");
        User user4 = baseServiceTest.createUser("test4", "test4");
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        userService.save(user4);
        List<UserDto> userDtoList = userService.findByName("test");
        assertThat(userDtoList, notNullValue());
        assertThat(userDtoList.size(), equalTo(4));
    }

    @Test
    public void findByNameWithInvalidNameTest() {
        User user1 = baseServiceTest.createUser("test1", "test1");
        User user2 = baseServiceTest.createUser("test2", "test2");
        User user3 = baseServiceTest.createUser("test3", "test3");
        User user4 = baseServiceTest.createUser("test4", "test4");
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);
        userService.save(user4);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findByName("Jhon"));
        assertThat(exception, notNullValue());
        assertThat(exception.getMessage(), equalTo("User with name Jhon not found"));
    }

    @Test
    public void saveUserTest() {
        User user = baseServiceTest.createUser("test", "test");
        userService.save(user);
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE id = :userId", User.class);
        User item = query.setParameter("userId", user.getId()).getSingleResult();
        assertThat(item, notNullValue());
        assertThat(item.getEmail(), equalTo(user.getEmail()));
        assertThat(item.getName(), equalTo(user.getName()));
    }

    @Test
    public void updateUserTest() {
        User user = baseServiceTest.createUser("test", "test");
        userService.save(user);
        UserUpdate newUser = new UserUpdate("test2", "test2");
        userService.update(newUser, user.getId());
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE id = :userId", User.class);
        User item = query.setParameter("userId", user.getId()).getSingleResult();
        assertThat(item, notNullValue());
        assertThat(item.getEmail(), equalTo(newUser.getEmail()));
        assertThat(item.getName(), equalTo(newUser.getName()));
    }

    @Test
    public void deleteUserTest() {
        User user = baseServiceTest.createUser("test", "test");
        userService.save(user);
        userService.delete(user.getId());
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE id = :userId", User.class);
        List<User> userList = query.setParameter("userId", user.getId()).getResultList();
        assertThat(userList, empty());
    }
}
