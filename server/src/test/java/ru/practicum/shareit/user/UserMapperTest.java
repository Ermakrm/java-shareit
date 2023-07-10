package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserListMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserMapperTest {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserListMapper userListMapper;
    User user;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("userEmail@yandex.ru");

        userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
    }

    @Test
    void toUserDto() {
        UserDto result = userMapper.toUserDto(user);

        assertEquals(userDto, result);
    }

    @Test
    void toUser() {
        User result = userMapper.toUser(userDto);

        assertEquals(user, result);
    }

    @Test
    void updateUserFromDto() {
        User userToUpdate = user;
        User dto = new User();
        dto.setName("newUserName");

        userMapper.updateUserFromDto(dto, userToUpdate);

        assertEquals(dto.getName(), userToUpdate.getName());
    }

    @Test
    void toUserDtoList() {
        User u = new User();
        u.setId(2L);
        u.setName("secondName");
        u.setEmail("secondEmail@yandex.ru");

        List<User> users = List.of(user, u);

        List<UserDto> userDtoList = userListMapper.toUserDtoList(users);

        assertEquals(2, userDtoList.size());
        assertEquals(userDtoList.get(0), userDto);
    }
}