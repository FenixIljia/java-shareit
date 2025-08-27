package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemRequestViewDto {

    @JsonProperty("id")
    private Long requestId;

    private String description;

    @JsonProperty("created")
    private LocalDateTime createDate;

    @JsonProperty("items")
    private List<ResponseViewDto> responseViewDtoList;

    public ItemRequestViewDto(Long requestId, String description, LocalDateTime createDate) {
        this.requestId = requestId;
        this.description = description;
        this.createDate = createDate;
        this.responseViewDtoList = new ArrayList<>();
    }
}
