package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundExceptions;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0L;

    private long getNextId() {
        return ++userId;
    }

    @Override
    public User findUser(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundExceptions("Пользователь не найден!");
        }
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            User updatedUser = users.get(user.getId());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setLogin(user.getLogin());
            if (user.getName() != null) {
                updatedUser.setName(user.getName());
            } else {
                updatedUser.setName(user.getLogin());
            }
            updatedUser.setBirthday(user.getBirthday());
            return updatedUser;
        } else {
            throw new NotFoundExceptions("Пользователь не найден!");
        }
    }
}
