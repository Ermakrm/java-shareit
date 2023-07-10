package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    static final String USER_HEADER = "X-Sharer-User-Id";
    final long userId = 1L;
    final long bookingId = 3L;
    final LocalDateTime start = LocalDateTime.now().plusDays(1);
    final LocalDateTime end = LocalDateTime.now().plusDays(2);
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    @MockBean
    BookingMapper bookingMapper;
    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(2L);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
    }

    @Test
    @SneakyThrows
    void create() {
        Booking booking = new Booking();
        BookingRequestDto requestToSave = bookingRequestDto;

        when(bookingMapper.toBooking(requestToSave)).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).create(booking, userId);
    }

/*    @Test
    @SneakyThrows
    void create_whenStartInThePast_thenReturnedBadRequest() {
        BookingRequestDto requestToSave = bookingRequestDto;
        requestToSave.setStart(LocalDateTime.now().minusMinutes(1));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void create_whenEndIsNow_thenReturnedBadRequest() {
        BookingRequestDto requestToSave = bookingRequestDto;
        requestToSave.setStart(LocalDateTime.now());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void create_whenItemIdIsNull_thenReturnedBadRequest() {
        BookingRequestDto requestToSave = bookingRequestDto;
        requestToSave.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(), anyLong());
    }
*/

    @Test
    @SneakyThrows
    void approve_whereApprovedIsTrue() {
        String approved = "true";

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId)
                        .param("approved", approved))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).approve(
                bookingId, userId, Boolean.valueOf(approved)
        );
    }

    @Test
    @SneakyThrows
    void approve_whereApprovedIsFalse() {
        String approved = "false";

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId)
                        .param("approved", approved))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).approve(
                bookingId, userId, Boolean.valueOf(approved)
        );
    }

    @Test
    @SneakyThrows
    void findById() {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findByIdAndUserId(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void findAllByUserIdAndState() {
        String state = "ALL";
        String from = "0";
        String size = "10";

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findByUserIdAndState(
                userId, state, Integer.parseInt(from), Integer.parseInt(size)
        );
    }

    @Test
    @SneakyThrows
    void findAllByUserIdAndState_withoutState_thenInvokedWithStateALL() {
        String state = "ALL";
        String from = "0";
        String size = "10";

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findByUserIdAndState(
                userId, state, Integer.parseInt(from), Integer.parseInt(size)
        );
    }
/*
    @Test
    @SneakyThrows
    void findAllByUserIdAndState_whenFromIsNegative_thenReturnedBadRequest() {
        String state = "ALL";
        String from = "-1";
        String size = "10";

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByUserIdAndState(
                anyLong(), anyString(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void findAllByUserIdAndState_whenSizeIsNegative_thenReturnedBadRequest() {
        String state = "ALL";
        String from = "0";
        String size = "-1";

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByUserIdAndState(
                anyLong(), anyString(), anyInt(), anyInt()
        );
    }


    @Test
    @SneakyThrows
    void findAllByUserIdAndState_whenSizeIsZero_thenReturnedBadRequest() {
        String state = "ALL";
        String from = "0";
        String size = "0";

        mockMvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByUserIdAndState(
                anyLong(), anyString(), anyInt(), anyInt()
        );
    }
*/

    @Test
    @SneakyThrows
    void findAllByOwnerIdAndState() {
        String state = "ALL";
        String from = "0";
        String size = "10";

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findByOwnerIdAndState(
                userId, state, Integer.parseInt(from), Integer.parseInt(size)
        );
    }

    @Test
    @SneakyThrows
    void findAllByOwnerIdAndState_withoutState_thenInvokedWithAll() {
        String state = "ALL";
        String from = "0";
        String size = "10";

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).findByOwnerIdAndState(
                userId, state, Integer.parseInt(from), Integer.parseInt(size)
        );
    }
/*
    @Test
    @SneakyThrows
    void findAllByOwnerIdAndState_whenFromIsNegative_thenReturnedBadRequest() {
        String state = "ALL";
        String from = "-1";
        String size = "10";

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwnerIdAndState(
                anyLong(), anyString(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void findAllByOwnerIdAndState_whenSizeIsNegative_thenReturnedBadRequest() {
        String state = "ALL";
        String from = "0";
        String size = "-1";

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwnerIdAndState(
                anyLong(), anyString(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void findAllByOwnerIdAndState_whenSizeIsZero_thenReturnedBadRequest() {
        String state = "ALL";
        String from = "0";
        String size = "0";

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByOwnerIdAndState(
                anyLong(), anyString(), anyInt(), anyInt()
        );
    }*/
}