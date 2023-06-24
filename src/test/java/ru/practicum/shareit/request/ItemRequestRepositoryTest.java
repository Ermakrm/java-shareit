package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    final PageRequest pageRequest = PageRequest.of(0, 10);
    @Autowired
    ItemRequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    User user1;
    User user2;

    ItemRequest request;
    ItemRequest request2;
    ItemRequest request3;

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
        ir.setRequester(user1);
        ir.setCreated(LocalDateTime.now());
        ir.setDescription("description");
        request = requestRepository.save(ir);

        ItemRequest ir2 = new ItemRequest();
        ir2.setRequester(user1);
        ir2.setCreated(LocalDateTime.now().plusDays(1));
        ir2.setDescription("secondDescription");
        request2 = requestRepository.save(ir2);

        ItemRequest ir3 = new ItemRequest();
        ir3.setRequester(user2);
        ir3.setCreated(LocalDateTime.now().plusDays(1));
        ir3.setDescription("secondDescription");
        request3 = requestRepository.save(ir3);
    }

    @Test
    void findAllByRequesterId_byUser1() {
        List<ItemRequest> expectedRequests = List.of(request, request2);

        List<ItemRequest> result = requestRepository.findAllByRequesterId(user1.getId());

        assertEquals(expectedRequests, result);
    }

    @Test
    void findAllByRequesterId_byUser2() {
        List<ItemRequest> expectedRequests = List.of(request3);

        List<ItemRequest> result = requestRepository.findAllByRequesterId(user2.getId());

        assertEquals(expectedRequests, result);
    }

    @Test
    void findAllByRequesterIdNot_byUser1() {

        List<ItemRequest> expectedRequests = List.of(request3);

        List<ItemRequest> result = requestRepository.findAllByRequesterIdNot(user1.getId(), pageRequest);

        assertEquals(expectedRequests, result);
    }

    @Test
    void findAllByRequesterIdNot_byUser2() {
        List<ItemRequest> expectedRequests = List.of(request, request2);

        List<ItemRequest> result = requestRepository.findAllByRequesterIdNot(user2.getId(), pageRequest);

        assertEquals(expectedRequests, result);
    }
}