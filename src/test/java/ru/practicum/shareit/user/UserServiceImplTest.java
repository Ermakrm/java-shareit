package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {
    @Mock
    UserMapper userMapper;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;
    User user1;

    User user2;

    @BeforeEach
    void setUp() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("firstUserName");
        u1.setEmail("firstEmail@yandex.ru");
        user1 = u1;

        User u2 = new User();
        u2.setId(2L);
        u2.setName("secondUserName");
        u2.setEmail("secondEmail@mail.ru");
        user2 = u2;
    }

    @Test
    void findAll_whenInvoked_thenReturnedUsersCollection() {
        List<User> users = List.of(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> all = userService.findAll();

        assertFalse(all.isEmpty());
        assertEquals(users, all);
    }

    @Test
    void create_whenUniqueEmail_thenSavedUser() {
        User userToSave = user1;
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.create(userToSave);

        verify(userRepository, times(1)).save(userToSave);
        assertEquals(userToSave, actualUser);
    }

    @Test
    void create_whenEmailAlreadyExist_thenEmailAlreadyExistThrown() {
        User userToSave = user1;
        when(userRepository.save(userToSave)).thenThrow(EmailAlreadyExistsException.class);

        EmailAlreadyExistsException e = assertThrows(
                EmailAlreadyExistsException.class, () -> userService.create(userToSave)
        );
        assertEquals("Email already exist", e.getMessage());
    }

    @Test
    void findById_whenUserFound_thenReturnedUser() {
        long userId = 1L;
        User expextedUser = user1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(expextedUser));

        User actualUser = userService.findById(userId);
        verify(userRepository, times(1)).findById(userId);
        assertEquals(expextedUser, actualUser);
    }

    @Test
    void findById_whenUserNotFound_thenUserNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(
                UserNotFoundException.class, () -> userService.findById(userId)
        );

        assertEquals(userNotFoundException.getMessage(), "User not found");
    }

    @Test
    void update_whenUserFound_thenUpdatedUserFields() {
        Long userId = user1.getId();
        User oldUser = user1;
        User newUser = user2;
        newUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User actualUser = userService.update(newUser, userId);

        verify(userMapper, times(1)).updateUserFromDto(newUser, oldUser);

        assertEquals(newUser, actualUser);
    }

    @Test
    void update_whenUserEmailNotUnique_thenEmailAlreadyExistThrown() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenThrow(EmailAlreadyExistsException.class);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(user1, user1.getId()));
    }


    @Test
    void delete() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}