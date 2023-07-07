package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    UserMapper userMapper;
    UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (RuntimeException e) {
            throw new EmailAlreadyExistsException("Email already exist");
        }
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User update(User user, Long id) {
        user.setId(id);
        User newUser = findById(id);
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
}
