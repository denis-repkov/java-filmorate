package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Выполнение запроса на получение всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User findUser(@PathVariable long id) {
        log.info("Выполнение запроса на получение пользователя с ID {}", id);
        return userService.findUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Выполнение запроса на создание пользователя " + newUser.getLogin());
        return userService.createUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Выполнение запроса на обновление пользователя " + newUser.getLogin());
        return userService.updateUser(newUser);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Выполнение запроса от пользователя с ID {} на добавление в друзья пользователя с ID {}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Выполнение запроса от пользователя с ID {} на удаление из друзей пользователя с ID {}", id, friendId);
        return userService.removeFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Выполнение запроса от пользователя с ID {} на получение списка друзей", id);
        return userService.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Выполнение запроса от пользователя с ID {} на получение списка общих друзей пользователя с ID {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}