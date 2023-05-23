package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userStorage.getUsers();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userStorage.getUser(userId));
    }

    @Override
    public UserDto updateUser(UserDto user, Long id) {
        user.setId(id);
        return UserMapper.toUserDto(userStorage.updateUser(UserMapper.toUser(user)));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public UserDto createUser(UserDto user) {
        return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(user)));
    }
}
