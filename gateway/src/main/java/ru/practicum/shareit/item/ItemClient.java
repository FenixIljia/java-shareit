package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemSaveDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.SaveComment;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAllByUserId(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findItemById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllByName(String text) {
        Map<String, Object> params = new HashMap<>();
        params.put("text", text);
        return get("", params);
    }

    public ResponseEntity<Object> save(ItemSaveDto itemSaveDto, long userId) {
        return post("", userId, itemSaveDto);
    }

    public ResponseEntity<Object> update(long itemId, ItemUpdateDto itemUpdateDto, long userId) {
        return patch("/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> save(SaveComment saveComment, long itemId, long userId) {
        return post("/" + itemId + "/comment", userId, saveComment);
    }
}
