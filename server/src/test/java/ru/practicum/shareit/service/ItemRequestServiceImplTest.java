package ru.practicum.shareit.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestsService;
import ru.practicum.shareit.request.dto.ItemRequestViewDto;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceImplTest {

    private final ItemRequestsService itemRequestsService;
    private final BaseServiceTest baseServiceTest = new BaseServiceTest();
    private final EntityManager em;
    @Autowired
    private UserService userService;

    @Test
    public void saveTest() {
        User user = userService.save(baseServiceTest.createUser("test", "test@mail.ru"));
        SaveItemRequestDto req = new SaveItemRequestDto(user.getId(), "Test");
        ItemRequest itemRequestSave = itemRequestsService.save(req);
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest i WHERE userId=:userId", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("userId", user)
                .getSingleResult();
        assertThat(itemRequest, notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestSave.getDescription()));
        assertThat(itemRequest.getId(), equalTo(itemRequestSave.getId()));
        assertThat(itemRequest.getUserId(), equalTo(itemRequestSave.getUserId()));
        assertThat(itemRequest.getCreateDate(), equalTo(itemRequestSave.getCreateDate()));
    }

    @Test
    public void findAllByUserIdTest() {
        User user = userService.save(baseServiceTest.createUser("test", "test@mail.ru"));
        SaveItemRequestDto req1 = new SaveItemRequestDto(user.getId(), "Test1");
        SaveItemRequestDto req2 = new SaveItemRequestDto(user.getId(), "Test2");
        SaveItemRequestDto req3 = new SaveItemRequestDto(user.getId(), "Test3");
        SaveItemRequestDto req4 = new SaveItemRequestDto(user.getId(), "Test4");
        itemRequestsService.save(req1);
        itemRequestsService.save(req2);
        itemRequestsService.save(req3);
        itemRequestsService.save(req4);
        List<ItemRequestViewDto> itemRequestViewDtoList = itemRequestsService.findAllByUserId(user.getId());
        assertThat(itemRequestViewDtoList, notNullValue());
        assertThat(itemRequestViewDtoList.size(), equalTo(4));
    }

    @Test
    public void findAllTest() {
        User user = userService.save(baseServiceTest.createUser("test", "test@mail.ru"));
        SaveItemRequestDto req1 = new SaveItemRequestDto(user.getId(), "Test1");
        SaveItemRequestDto req2 = new SaveItemRequestDto(user.getId(), "Test2");
        SaveItemRequestDto req3 = new SaveItemRequestDto(user.getId(), "Test3");
        SaveItemRequestDto req4 = new SaveItemRequestDto(user.getId(), "Test4");
        itemRequestsService.save(req1);
        itemRequestsService.save(req2);
        itemRequestsService.save(req3);
        itemRequestsService.save(req4);
        List<ItemRequestViewDto> itemRequestViewDtoList = itemRequestsService.findAll();
        assertThat(itemRequestViewDtoList, notNullValue());
        assertThat(itemRequestViewDtoList.size(), equalTo(4));
    }

    @Test
    public void findByIdTest() {
        User user = userService.save(baseServiceTest.createUser("test", "test@mail.ru"));
        SaveItemRequestDto req1 = new SaveItemRequestDto(user.getId(), "Test1");
        ItemRequest itemRequest = itemRequestsService.save(req1);
        ItemRequestViewDto itemRequestViewDto = itemRequestsService.findById(itemRequest.getId());
        assertThat(itemRequestViewDto, notNullValue());
        assertThat(itemRequestViewDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequest.getUserId(), equalTo(itemRequest.getUserId()));
        assertThat(itemRequest.getCreateDate(), equalTo(itemRequest.getCreateDate()));
    }
}