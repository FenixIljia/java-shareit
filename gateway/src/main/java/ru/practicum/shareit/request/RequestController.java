package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HttpHeaders;
import ru.practicum.shareit.request.dto.SaveItemRequestDto;

@RestController
@AllArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestClient requestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        return requestClient.findAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findAllByRequestId(@PathVariable long requestId) {
        return requestClient.findAllByRequestId(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(HttpHeaders.USER_ID_HEADER) long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(
            @RequestBody SaveItemRequestDto saveItemRequestDto,
            @RequestHeader(HttpHeaders.USER_ID_HEADER) long userId
    ) {
        return requestClient.save(saveItemRequestDto, userId);
    }
}
