package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User findUser(long id);

    List<User> findAllUsers();
}
