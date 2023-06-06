package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User update(UserDto user, Long id) {
        user.setId(id);
        User newUser = userRepository.findById(id).orElseThrow();
        userMapper.updateUserFromDto(user, newUser);
        try {
            return userRepository.save(newUser);
        } catch (RuntimeException e) {
            throw new EmailAlreadyExistsException("Email already exist");
        }
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User create(UserDto user) {
        try {
            return userRepository.save(userMapper.toUser(user));
        } catch (RuntimeException e) {
            throw new EmailAlreadyExistsException("Email already exist");
        }

    }
}
