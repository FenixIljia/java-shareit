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
import ru.practicum.shareit.controller.dto.ItemViewImpl;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private User user = new User(1L, "test", "test");

    private Item item = new Item(1L, user, "test", "test", true, null);

    @Test
    public void getItemByUserIdTest() throws Exception {
        when(itemService.findAllByUserId(anyLong()))
                .thenReturn(createItemViewOwnerList());
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(4L), Long.class));
    }

    @Test
    public void getItemByIdTest() throws Exception {
        ItemViewOwner itemViewOwner = new ItemViewOwner();
        itemViewOwner.setDescription(item.getDescription());
        itemViewOwner.setAvailable(item.getAvailable());
        itemViewOwner.setItemId(item.getItemId());
        itemViewOwner.setName(item.getName());
        itemViewOwner.setOwnerId(item.getUser().getId());
        when(itemService.findById(anyLong(), anyLong()))
                .thenReturn(itemViewOwner);
        mvc.perform(get("/items/" + item.getItemId())
                        .header("X-Sharer-User-Id", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemViewOwner.getDescription())))
                .andExpect(jsonPath("$.available", is(itemViewOwner.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(1)))
                .andExpect(jsonPath("$.name", is(itemViewOwner.getName())));
    }

    @Test
    public void getItemByNameTest() throws Exception {
        when(itemService.findAllByName(anyString()))
                .thenReturn(createItemViewList());
        mvc.perform(get("/items/search")
                        .param("text", item.getName())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(4L), Long.class));
    }

    @Test
    public void createItemTest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setItemId(item.getItemId());
        itemDto.setName(item.getName());
        when(itemService.save(any(ItemDto.class), anyLong()))
                .thenReturn(item);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemUpdate itemUpdate = new ItemUpdate();
        itemUpdate.setDescription(item.getDescription());
        itemUpdate.setAvailable(item.getAvailable());
        itemUpdate.setName(item.getName());
        when(itemService.update(any(ItemUpdate.class), anyLong(), anyLong()))
                .thenReturn(item);
        mvc.perform(patch("/items/" + item.getItemId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(itemUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.user.name", is(item.getUser().getName())))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.email", is(item.getUser().getEmail())));
    }

    @Test
    public void saveCommentTest() throws Exception {
        SaveComment saveComment = new SaveComment("Test");
        CommentView commentView = new CommentView();
        commentView.setItemId(item.getItemId());
        commentView.setId(1L);
        commentView.setAuthorName(user.getName());
        commentView.setText(saveComment.getText());
        commentView.setCreated(LocalDateTime.of(2000, 10, 10, 10, 10));
        when(itemService.save(any(SaveComment.class), anyLong(), anyLong()))
                .thenReturn(commentView);
        mvc.perform(post("/items/" + item.getItemId() + "/comment")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(saveComment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is(commentView.getText())))
                .andExpect(jsonPath("$.authorName", is(commentView.getAuthorName())))
                .andExpect(jsonPath("$.created", is("2000-10-10T10:10:00")))
                .andExpect(jsonPath("$.itemId", is(1)));
    }

    private List<ItemViewOwner> createItemViewOwnerList() {
        ItemViewOwner itemViewOwner1 = new ItemViewOwner();
        itemViewOwner1.setItemId(item.getItemId());
        itemViewOwner1.setName(item.getName());
        itemViewOwner1.setOwnerId(item.getUser().getId());
        itemViewOwner1.setAvailable(item.getAvailable());
        itemViewOwner1.setDescription(item.getDescription());
        ItemViewOwner itemViewOwner2 = new ItemViewOwner();
        itemViewOwner2.setItemId(item.getItemId());
        itemViewOwner2.setName(item.getName());
        itemViewOwner2.setOwnerId(item.getUser().getId());
        itemViewOwner2.setAvailable(item.getAvailable());
        itemViewOwner2.setDescription(item.getDescription());
        ItemViewOwner itemViewOwner3 = new ItemViewOwner();
        itemViewOwner3.setItemId(item.getItemId());
        itemViewOwner3.setName(item.getName());
        itemViewOwner3.setOwnerId(item.getUser().getId());
        itemViewOwner3.setAvailable(item.getAvailable());
        itemViewOwner3.setDescription(item.getDescription());
        ItemViewOwner itemViewOwner4 = new ItemViewOwner();
        itemViewOwner4.setItemId(item.getItemId());
        itemViewOwner4.setName(item.getName());
        itemViewOwner4.setOwnerId(item.getUser().getId());
        itemViewOwner4.setAvailable(item.getAvailable());
        itemViewOwner4.setDescription(item.getDescription());
        return List.of(itemViewOwner1, itemViewOwner2, itemViewOwner3, itemViewOwner4);
    }

    private List<ItemView> createItemViewList() {
        ItemViewImpl itemView1 = new ItemViewImpl(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        ItemViewImpl itemView2 = new ItemViewImpl(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        ItemViewImpl itemView3 = new ItemViewImpl(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        ItemViewImpl itemView4 = new ItemViewImpl(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
        return List.of(itemView1, itemView2, itemView3, itemView4);
    }
}
