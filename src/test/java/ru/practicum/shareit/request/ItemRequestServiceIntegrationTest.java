package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {"db.name=test"})
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceIntegrationTest {

    @Autowired
    ItemRequestService requestService;
    @Autowired
    UserService userService;
    User user1;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setName("firstUserName");
        u.setEmail("firstEmail@yandex.ru");
        user1 = userService.create(u);

        ItemRequestRequestDto ir = new ItemRequestRequestDto();
        ir.setDescription("description");
        request = requestService.save(ir, user1.getId());

    }

    @Test
    void save() {
        ItemRequestRequestDto requestToSave = new ItemRequestRequestDto();
        requestToSave.setDescription("descriptionToSave");

        ItemRequest result = requestService.save(requestToSave, user1.getId());

        assertEquals(requestToSave.getDescription(), result.getDescription());
        assertEquals(user1, result.getRequester());
    }

    @Test
    void findByUserId() {
        ItemRequest expectedRequest = request;

        List<ItemRequestResponseDto> result = requestService.findByUserId(user1.getId());

        assertEquals(1, result.size());
        assertEquals(expectedRequest.getId(), result.get(0).getId());
        assertEquals(expectedRequest.getDescription(), result.get(0).getDescription());
    }

    @Test
    void findById() {
        ItemRequest expectedRequest = request;

        ItemRequestResponseDto result = requestService.findById(expectedRequest.getId(), user1.getId());

        assertEquals(expectedRequest.getId(), result.getId());
        assertEquals(expectedRequest.getDescription(), result.getDescription());
    }

    @Test
    void getItemRequestById() {
        ItemRequest expectedRequest = request;

        ItemRequest result = requestService.getItemRequestById(expectedRequest.getId());

        assertEquals(expectedRequest, result);
    }
}