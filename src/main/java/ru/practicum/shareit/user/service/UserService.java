package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    List<User> findAll();

    User findById(Long userId);

    User update(UserDto user, Long id);

    void delete(Long userId);

    User create(UserDto user);
}
