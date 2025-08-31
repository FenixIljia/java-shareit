package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestsService;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestsService itemRequestsService;

    @Test
    public void findAllTest() throws Exception {
        when(itemRequestsService.findAll())
                .thenReturn(creatItemRequestViewDtoList());
        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4));
    }

    @Test
    public void findByRequestIdTest() throws Exception {
        ItemRequestViewDto itemRequestViewDto = new ItemRequestViewDto();
        itemRequestViewDto.setRequestId(1L);
        itemRequestViewDto.setDescription("test");
        when(itemRequestsService.findById(anyLong()))
                .thenReturn(itemRequestViewDto);
        mvc.perform(get("/requests/" + itemRequestViewDto.getRequestId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("test"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void findAllByUserId() throws Exception {
        when(itemRequestsService.findAllByUserId(anyLong()))
                .thenReturn(creatItemRequestViewDtoList());
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4));
    }

    @Test
    public void saveItemRequestTest() throws Exception {
        SaveItemRequestDto saveItemRequestDto = new SaveItemRequestDto(1L, "test");
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test");
        itemRequest.setUserId(user);
        when(itemRequestsService.save(any(SaveItemRequestDto.class)))
                .thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(mapper.writeValueAsString(saveItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("test"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId.id").value(1));
    }

    private List<ItemRequestViewDto> creatItemRequestViewDtoList() {
        ItemRequestViewDto itemRequestViewDto1 = new ItemRequestViewDto();
        itemRequestViewDto1.setDescription("test");
        ItemRequestViewDto itemRequestViewDto2 = new ItemRequestViewDto();
        itemRequestViewDto2.setDescription("test");
        ItemRequestViewDto itemRequestViewDto3 = new ItemRequestViewDto();
        itemRequestViewDto3.setDescription("test");
        ItemRequestViewDto itemRequestViewDto4 = new ItemRequestViewDto();
        itemRequestViewDto4.setDescription("test");
        ItemRequestViewDto itemRequestViewDto5 = new ItemRequestViewDto();
        itemRequestViewDto5.setDescription("test");
        return List.of(itemRequestViewDto1, itemRequestViewDto2, itemRequestViewDto3, itemRequestViewDto4);
    }
}
