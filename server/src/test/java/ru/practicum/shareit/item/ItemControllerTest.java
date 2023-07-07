package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemListMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    static final String USER_HEADER = "X-Sharer-User-Id";
    final long userId = 1L;
    final long itemId = 2L;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @MockBean
    ItemMapper itemMapper;
    @MockBean
    ItemListMapper itemListMapper;
    @MockBean
    CommentMapper commentMapper;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("itemName");
        itemDto.setDescription("itemDescription");
        itemDto.setAvailable(true);
    }

    @Test
    @SneakyThrows
    void createItem() {
        ItemDto itemToCreate = itemDto;

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).create(itemDto, userId);
    }

    @Test
    @SneakyThrows
    void createItem_whenNameIsNull_thenReturnedBadRequest() {
        ItemDto itemToCreate = itemDto;
        itemToCreate.setName(null);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(any(), any());
    }

    @Test
    @SneakyThrows
    void createItem_whenDescriptionIsNull_thenReturnedBadRequest() {
        ItemDto itemToCreate = itemDto;
        itemToCreate.setDescription(null);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(any(), any());
    }

    @Test
    @SneakyThrows
    void createItem_whenAvailableIsNull_thenReturnedBadRequest() {
        ItemDto itemToCreate = itemDto;
        itemToCreate.setAvailable(null);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(any(), any());
    }

    @Test
    @SneakyThrows
    void updateItem() {
        ItemDto itemToUpdate = itemDto;

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToUpdate))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).update(
                itemDto, itemId, userId);
    }

    @Test
    @SneakyThrows
    void getItem() {
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).findByIdWithBookings(itemId, userId);
    }

    @Test
    @SneakyThrows
    void getAllItems() {
        String from = "0";
        String size = "10";

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isOk());

        verify(itemService, times(1)).findAllByOwnerId(
                userId, Integer.parseInt(from), Integer.parseInt(size)
        );
    }

    @Test
    @SneakyThrows
    void getAllItems_withoutParams_thenInvokedWithFrom0AndSize20() {
        String from = "0";
        String size = "20";

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).findAllByOwnerId(
                userId, Integer.parseInt(from), Integer.parseInt(size)
        );
    }

    @Test
    @SneakyThrows
    void getAllItems_whenFromIsNegative_thenReturnedBadRequest() {
        String from = "-1";
        String size = "10";

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAllByOwnerId(
                anyLong(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void getAllItems_whenSizeIsNegative_thenReturnedBadRequest() {
        String from = "0";
        String size = "-1";

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAllByOwnerId(
                anyLong(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void getAllItems_whenSizeZero_thenReturnedBadRequest() {
        String from = "0";
        String size = "0";

        mockMvc.perform(get("/items")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAllByOwnerId(
                anyLong(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void search() {
        String from = "0";
        String size = "10";
        String text = "text ";

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size)
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemService, times(1)).search(
                text, Integer.parseInt(from), Integer.parseInt(size)
        );
    }

    @Test
    @SneakyThrows
    void search_withoutParams_thenInvokedWithFrom0AndSize20() {
        String from = "0";
        String size = "20";
        String text = "text ";

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, userId)
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemService, times(1)).search(
                text, Integer.parseInt(from), Integer.parseInt(size)
        );
    }

    @Test
    @SneakyThrows
    void search_whenFromIsNegative_thenReturnedBadRequest() {
        String from = "-1";
        String size = "20";
        String text = "text ";

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size)
                        .param("text", text))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).search(
                anyString(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void search_whenSizeZero_thenReturnedBadRequest() {
        String from = "0";
        String size = "0";
        String text = "text ";

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER, userId)
                        .param("from", from)
                        .param("size", size)
                        .param("text", text))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).search(
                anyString(), anyInt(), anyInt()
        );
    }

    @Test
    @SneakyThrows
    void addComment() {
        CommentRequestDto comment = new CommentRequestDto();
        comment.setText("text ");

        Comment com = new Comment();
        com.setText("text ");

        when(commentMapper.toComment(comment)).thenReturn(com);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).addComment(userId, itemId, com);
    }

    @Test
    @SneakyThrows
    void addComment_whenCommentIsNull_thenReturnedBadRequest() {
        CommentRequestDto comment = new CommentRequestDto();

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment))
                        .header(USER_HEADER, userId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(anyLong(), anyLong(), any());
    }
}