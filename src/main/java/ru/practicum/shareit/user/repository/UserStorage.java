package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUser(Long id);

    User addUser(User user);

    void deleteUser(Long id);

    User updateUser(User user);
}
