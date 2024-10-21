package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundExceptions;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User findUser(long id) {
        return userStorage.findUser(id);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        userValidation(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.findUser(user.getId()) == null) {
            throw new NotFoundExceptions("Пользователь не найден!");
        }
        userValidation(user);
        return userStorage.updateUser(user);
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        if (user == null || friend == null) {
            log.error("Пользователь для добавление в друзья не найден");
            throw new NotFoundExceptions("Пользователь не найден");
        }
        log.info("Пользователь с ID {} добавил в друзья пользователя с ID {}", userId, friendId);
        userStorage.addFriend(userId, friendId);
        return user;
    }

    public User removeFriend(long userId, long friendId) {
        User user = userStorage.findUser(userId);
        User friend = userStorage.findUser(friendId);
        if (user == null || friend == null) {
            log.error("Пользователь для удаления из друзей не найден");
            throw new NotFoundExceptions("Пользователь не найден");
        }
        if (user.getFriends() != null && friend.getFriends() != null && user.getFriends().contains(friendId)) {
            log.info("Пользователи с ID {} удалил из друзей пользователя с ID {}", userId, friendId);
            userStorage.deleteFriend(userId, friendId);
        }
        return user;
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.findUser(userId);
        if (user == null) {
            log.error("Пользователь для получения списка друзей не найден");
            throw new NotFoundExceptions("Пользователь не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::findUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        User user = findUser(userId);
        User otherUser = findUser(otherUserId);
        if (user == null || otherUser == null) {
            log.error("Пользователь для получения списка общих друзей не найден");
            throw new NotFoundExceptions("Пользователь не найден");
        }
        return (List<User>) userStorage.getCommonFriends(userId, otherUserId);
    }

    private void userValidation(User newUser) {
        if (newUser == null) {
            log.error("Попытка создания нового пользователя с пустым объектом");
            throw new ValidationExceptions("Необходимо заполнить все данные");
        }
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            log.error("Пользователь не указал адрес эл.почты");
            throw new ValidationExceptions("Адрес эл.почты не указан");
        }
        if (!newUser.getEmail().contains("@")) {
            log.error("Пользователь указал некорректный адрес эл.почты");
            throw new ValidationExceptions("Адрес эл.почты не содержит @");
        }
        if (newUser.getLogin().isEmpty() || newUser.getLogin().contains(" ")) {
            log.error("Пользователь указал некорректный логин");
            throw new ValidationExceptions("Логин не может быть пустым и/или содержать пробелы");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Пользователь указал некорректную дату рождения");
            throw new ValidationExceptions("Дата рождения не может быть в будущем");
        }
    }
}
