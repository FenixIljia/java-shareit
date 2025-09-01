package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdate;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Test
    public void findAllTest() throws Exception {
        when(userService.findAll())
                .thenReturn(createUserDtoList());
        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3L), Long.class));
    }

    @Test
    public void findByIdTest() throws Exception {
        UserDto userDto = UserDto.builder().id(1L).email("email").name("test").build();
        when(userService.findById(anyLong()))
                .thenReturn(userDto);
        mvc.perform(get("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("email")));
    }

    @Test
    public void createUserTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("email");
        when(userService.save(any(User.class)))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("email")));
    }

    @Test
    public void updateUserTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("email");
        when(userService.update(any(UserUpdate.class), anyLong()))
                .thenReturn(user);
        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test")))
                .andExpect(jsonPath("$.email", is("email")));
    }

    @Test
    public void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/" + 1L)
        .characterEncoding(StandardCharsets.UTF_8)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private List<UserDto> createUserDtoList() {
        UserDto userDto1 = UserDto.builder().id(1L).email("email").name("name").build();
        UserDto userDto2 = UserDto.builder().id(2L).email("email2").name("name").build();
        UserDto userDto3 = UserDto.builder().id(3L).email("email3").name("name").build();
        return List.of(userDto1, userDto2, userDto3);
    }
}
