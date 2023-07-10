package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.exception.UserNotFoundException;
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
class UserServiceIntegrationTest {
    @Autowired
    UserService userService;

    User user1;

    User user2;

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("firstUserName");
        u1.setEmail("firstEmail@yandex.ru");
        user1 = userService.create(u1);

        User u2 = new User();
        u2.setId(2L);
        u2.setName("secondUserName");
        u2.setEmail("secondEmail@mail.ru");
        user2 = userService.create(u2);
    }

    @Test
    void create() {
        User user = new User();
        user.setId(10L);
        user.setName("newUserName");
        user.setEmail("newUserEmail@yandex.ru");

        User result = userService.create(user);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }


    @Test
    void update() {
        User userToUpdate = user1;
        userToUpdate.setEmail("updatedEmail@email.com");
        userToUpdate.setName("updatedName");

        User result = userService.update(userToUpdate, userToUpdate.getId());

        assertEquals(userToUpdate, result);
    }

    @Test
    void findById() {
        User expectedUser = user1;

        User result = userService.findById(expectedUser.getId());

        assertEquals(expectedUser, result);
    }

    @Test
    void findAll() {
        List<User> expectedUsers = List.of(user1, user2);

        List<User> result = userService.findAll();

        assertEquals(expectedUsers, result);
    }

    @Test
    void delete() {
        userService.delete(user1.getId());
        assertThrows(UserNotFoundException.class, () -> userService.findById(user1.getId()));
    }
}