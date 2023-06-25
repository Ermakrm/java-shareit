package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.mapper.CommentListMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemListMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ItemMapperTest {
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    ItemListMapper itemListMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    CommentListMapper commentListMapper;

    Item item;
    ItemDto itemDto;
    ItemResponseDto itemResponseDto;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(true);

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(true);
    }

    @Test
    void toItemDto() {
        ItemDto result = itemMapper.toItemDto(item);

        assertEquals(itemDto, result);
    }

    @Test
    void toItem() {
        Item result = itemMapper.toItem(itemDto);

        assertEquals(item, result);
    }

    @Test
    void toItemResponse() {
        ItemResponseDto result = itemMapper.toItemResponse(item);

        assertEquals(itemResponseDto, result);
    }

    @Test
    void updateItemFromDto() {
        Item itemToUpdate = item;
        ItemDto dto = new ItemDto();
        dto.setName("newItemName");

        itemMapper.updateItemFromDto(dto, itemToUpdate);

        assertEquals(dto.getName(), itemToUpdate.getName());
        assertNotNull(itemToUpdate.getId());
        assertNotNull(itemToUpdate.getDescription());
        assertNotNull(itemToUpdate.getAvailable());
    }

    @Test
    void toItemDtoList() {
        List<Item> itemList = List.of(item, new Item());

        List<ItemDto> result = itemListMapper.toItemDtoList(itemList);

        assertEquals(itemDto, result.get(0));
        assertEquals(2, result.size());
    }

    @Test
    void toItemResponseDtoList() {
        List<Item> itemList = List.of(item, new Item());

        List<ItemResponseDto> result = itemListMapper.toItemResponseDtoList(itemList);

        assertEquals(itemResponseDto, result.get(0));
        assertEquals(2, result.size());
    }

    @Test
    void toComment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("text ");

        Comment result = commentMapper.toComment(commentRequestDto);

        assertEquals(commentRequestDto.getText(), result.getText());
    }

    @Test
    void toCommentResponseDto() {
        User author = new User();
        author.setName("authorName");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setText("text ");

        CommentResponseDto result = commentMapper.toCommentResponseDto(comment);

        assertEquals(author.getName(), result.getAuthorName());
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    void toCommentResponseList() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setCreated(LocalDateTime.now());
        comment.setText("text ");

        List<Comment> comments = List.of(comment, new Comment());

        List<CommentResponseDto> result = commentListMapper.toCommentResponseList(comments);

        assertEquals(2, result.size());
        assertEquals(comment.getId(), result.get(0).getId());
        assertEquals(comment.getCreated(), result.get(0).getCreated());
    }
}