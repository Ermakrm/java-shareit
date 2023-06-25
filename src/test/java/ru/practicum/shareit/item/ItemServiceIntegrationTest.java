package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exception.IllegalCommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = {"db.name=test"})
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceIntegrationTest {

    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;

    User user1;
    Item item1;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setName("firstUserName");
        u.setEmail("firstEmail@yandex.ru");
        user1 = userService.create(u);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("firstItemName");
        itemDto.setDescription("firstItemDescription");
        itemDto.setAvailable(true);
        item1 = itemService.create(itemDto, user1.getId());
    }

    @Test
    void create() {
        ItemDto itemToCreate = new ItemDto();
        itemToCreate.setId(10L);
        itemToCreate.setDescription("createdDescription");
        itemToCreate.setName("createdName");
        itemToCreate.setAvailable(true);

        Item result = itemService.create(itemToCreate, user1.getId());

        assertEquals(itemToCreate.getDescription(), result.getDescription());
    }

    @Test
    void update() {
        ItemDto expectedItem = new ItemDto();
        expectedItem.setName("updatedName");
        expectedItem.setDescription("updatedDescription");

        Item result = itemService.update(expectedItem, item1.getId(), user1.getId());

        assertEquals(expectedItem.getName(), result.getName());
        assertEquals(expectedItem.getDescription(), result.getDescription());
    }

    @Test
    void findById() {
        Item expectedItem = item1;

        Item result = itemService.findById(expectedItem.getId());

        assertEquals(expectedItem, result);
    }

    @Test
    void search() {
        Item expectedItem = item1;
        String text = "des";

        List<Item> result = itemService.search(text, 0, 5);

        assertEquals(1, result.size());
        assertEquals(expectedItem.getId(), result.get(0).getId());
    }

    @Test
    void findByIdWithBookings() {
        Item expected = item1;

        ItemResponseDto result = itemService.findByIdWithBookings(item1.getId(), user1.getId());

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getDescription(), result.getDescription());
    }

    @Test
    void findAllByOwnerId() {
        Item expected = item1;

        List<ItemResponseDto> result = itemService.findAllByOwnerId(user1.getId(), 0, 10);

        assertEquals(expected.getId(), result.get(0).getId());
        assertEquals(expected.getDescription(), result.get(0).getDescription());
    }

    @Test
    void addComment() {
        assertThrows(IllegalCommentException.class,
                () -> itemService.addComment(user1.getId(), item1.getId(), new Comment()));

    }

}