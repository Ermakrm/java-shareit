package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {
    static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestMapper requestMapper;
    @MockBean
    ItemRequestService service;
    ItemRequestRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestRequestDto();
        requestDto.setDescription("description");
    }

    @SneakyThrows
    @Test
    void addRequest() {
        long userId = 1L;
        ItemRequestRequestDto requestToSave = requestDto;

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).save(requestDto, userId);
    }

/*    @SneakyThrows
    @Test
    void addRequest_whenDescriptionsIsEmpty_thenReturnedBadRequest() {
        long userId = 1L;
        ItemRequestRequestDto requestToSave = requestDto;
        requestToSave.setDescription(null);

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(requestDto, userId);
    }*/

    @SneakyThrows
    @Test
    void findByUserId() {
        long userId = 1L;
        mockMvc.perform(get("/requests").header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).findByUserId(userId);
    }

    @SneakyThrows
    @Test
    void findAllWithPagination() {
        long userId = 1L;
        String from = "0";
        String size = "10";
        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());

        verify(service, times(1)).findAllWithPagination(
                userId, Integer.parseInt(from), Integer.parseInt(size));

    }

    @SneakyThrows
    @Test
    void findAllWithPagination_withoutParams_thenMustInvokerFrom0WithSize20() {
        long userId = 1L;

        mockMvc.perform(get("/requests/all")
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).findAllWithPagination(
                userId, 0, 20);

    }

    @SneakyThrows
    @Test
    void findById() {
        long requestId = 1L;
        long userId = 2L;
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(requestId, userId);
    }
}