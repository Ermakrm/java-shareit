package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Map<Long, User> users = new HashMap<>();
    private static long generatorUserId = 0;
    private final Set<String> emails = new HashSet<>();

    public static boolean isUserExist(Long userId) {
        return users.containsKey(userId);
    }

    public static User getUserById(Long id) {
        return users.get(id);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private long getNextId() {
        return ++generatorUserId;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ValidationException("Email занят");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        log.debug("User added {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId()) || user.getId() == null) {
            throw new ValidationException("Ошибка обновления! Такого пользователя не существует");
        }
        User userInMemory = users.get(user.getId());
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(userInMemory.getEmail())) {
                if (emails.contains(user.getEmail())) {
                    throw new ValidationException("Email занят");
                }
                emails.remove(userInMemory.getEmail());
                emails.add(user.getEmail());
            }
        } else {
            user.setEmail(users.get(user.getId()).getEmail());
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        users.put(user.getId(), user);
        log.debug("User updated {}", user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (users.containsKey(id)) {
            User deletedUser = users.remove(id);
            emails.remove(deletedUser.getEmail());
            log.debug("User deleted {}", deletedUser);
        } else {
            throw new ValidationException("Ошибка! Такого пользователя не существует");
        }
    }
}

