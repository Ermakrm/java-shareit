package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingInfoDtoJsonTest {

    @Autowired
    JacksonTester<BookingInfoDto> jacksonTester;


    @Test
    @SneakyThrows
    void bookingInfoDtoTest() {
        BookingInfoDto bookingInfoDto = new BookingInfoDto();
        bookingInfoDto.setId(1L);
        bookingInfoDto.setBookerId(2L);
        bookingInfoDto.setStart(LocalDateTime.now());
        bookingInfoDto.setEnd(LocalDateTime.now());

        JsonContent<BookingInfoDto> result = jacksonTester.write(bookingInfoDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }
}