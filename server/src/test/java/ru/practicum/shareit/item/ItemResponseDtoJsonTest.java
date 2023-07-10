package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoJsonTest {
    @Autowired
    JacksonTester<ItemResponseDto> jacksonTester;

    @SneakyThrows
    @Test
    void itemResponseDtoTest() {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setDescription("itemDescription");
        itemResponseDto.setAvailable(true);
        itemResponseDto.setLastBooking(new BookingInfoDto());
        itemResponseDto.setNextBooking(new BookingInfoDto());
        itemResponseDto.setComments(new ArrayList<>());
        itemResponseDto.setRequestId(2L);

        JsonContent<ItemResponseDto> result = jacksonTester.write(itemResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemResponseDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemResponseDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemResponseDto.getAvailable());
    }

}
/*    Long id;
    String name;
    String description;
    Boolean available;
    BookingInfoDto lastBooking;
    BookingInfoDto nextBooking;
    List<CommentResponseDto> comments;
    Long requestId;*/