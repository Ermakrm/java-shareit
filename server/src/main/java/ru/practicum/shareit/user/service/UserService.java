package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    List<User> findAll();

    User findById(Long userId);

    User update(User user, Long id);

    void delete(Long userId);

    User create(User user);
}
