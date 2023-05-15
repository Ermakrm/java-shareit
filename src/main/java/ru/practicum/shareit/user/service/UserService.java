package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

    UserDto updateUser(UserDto user, Long id);

    void deleteUser(Long userId);

    UserDto createUser(UserDto user);
}
