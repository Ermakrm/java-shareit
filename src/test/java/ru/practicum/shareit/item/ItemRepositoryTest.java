package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    final PageRequest pageRequest = PageRequest.of(0, 10);
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository requestRepository;
    User user1;
    User user2;

    Item item1;
    Item item2;
    Item item3;

    ItemRequest request;
    ItemRequest request2;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setName("firstUserName");
        u.setEmail("firstEmail@yandex.ru");
        user1 = userRepository.save(u);

        User u2 = new User();
        u2.setName("secondUserName");
        u2.setEmail("secondUserEmail");
        user2 = userRepository.save(u2);

        ItemRequest ir = new ItemRequest();
        ir.setRequester(user2);
        ir.setCreated(LocalDateTime.now());
        ir.setDescription("description");
        request = requestRepository.save(ir);

        ItemRequest ir2 = new ItemRequest();
        ir2.setRequester(user2);
        ir2.setCreated(LocalDateTime.now().plusDays(1));
        ir2.setDescription("secondDescription");
        request2 = requestRepository.save(ir2);

        Item i = new Item();
        i.setName("firstItemName");
        i.setDescription("alfa");
        i.setAvailable(true);
        i.setOwner(user1);
        i.setRequest(request);
        item1 = itemRepository.save(i);

        Item i2 = new Item();
        i2.setName("secondItemName");
        i2.setDescription("beta");
        i2.setAvailable(true);
        i2.setOwner(user1);
        i2.setRequest(request2);
        item2 = itemRepository.save(i2);

        Item i3 = new Item();
        i3.setName("thirdItemName");
        i3.setDescription("omega");
        i3.setAvailable(true);
        i3.setOwner(user2);
        item3 = itemRepository.save(i3);
    }

    @Test
    void findAllByOwnerIdOrderById_whenUser1() {
        List<Item> expectedItems = List.of(item1, item2);

        List<Item> itemsByUser1 = itemRepository.findAllByOwnerIdOrderById(
                user1.getId(), pageRequest
        );

        assertEquals(expectedItems, itemsByUser1);
    }

    @Test
    void findAllByOwnerIdOrderById_whenUser2() {
        List<Item> expectedItems = List.of(item3);

        List<Item> itemsByUser1 = itemRepository.findAllByOwnerIdOrderById(
                user2.getId(), pageRequest
        );

        assertEquals(expectedItems, itemsByUser1);
    }

    @Test
    void findAllByOwnerIdOrderById_whenUserDoesNotExist_thenReturnedEmptyList() {
        List<Item> itemsByUser1 = itemRepository.findAllByOwnerIdOrderById(
                10L, pageRequest
        );

        assertTrue(itemsByUser1.isEmpty());
    }

    @Test
    void search_byName() {
        List<Item> expectedItems = List.of(item1, item2, item3);
        String text = "iTeMnAMe";

        List<Item> result = itemRepository.search(text, pageRequest);

        assertEquals(expectedItems, result);
    }

    @Test
    void search_byDescription() {
        List<Item> expectedItems = List.of(item1);
        String text = "al";

        List<Item> result = itemRepository.search(text, pageRequest);

        assertEquals(expectedItems, result);
    }

    @Test
    void findAllByRequestId_shouldReturnItem1() {
        List<Item> expectedRequests = List.of(item1);

        List<Item> result = itemRepository.findAllByRequestId(request.getId());

        assertEquals(expectedRequests, result);
    }

    @Test
    void findAllByRequestId_shouldReturnItem2() {
        List<Item> expectedRequests = List.of(item2);

        List<Item> result = itemRepository.findAllByRequestId(request2.getId());

        assertEquals(expectedRequests, result);
    }
}