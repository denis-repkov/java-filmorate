package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Service
public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User findUser(long id);

    List<User> findAllUsers();

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getCommonFriends(Long userId, Long otherId);

    List<User> getAllFriends(Long id);
}
