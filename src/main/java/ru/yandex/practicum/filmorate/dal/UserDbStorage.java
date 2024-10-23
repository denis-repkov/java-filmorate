package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("userStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String INSERT_USER_QUERY = "INSERT INTO users (name, login, email, birthday) VALUES (?, ?, ?, ?)";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String GET_ONE_USER_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friendship_statuses (user_id, friend_id, status) " +
            "VALUES (?, ?, false)";
    private static final String UPDATE_FRIEND_QUERY = "UPDATE friendship_statuses SET user_id = ?, friend_id = ?, status = true " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_FRIEND_QUERY_WHEN_DELETE = "UPDATE friendship_statuses SET status = false " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship_statuses " +
            "WHERE (user_id = ? AND friend_id = ?)";
    private static final String GET_ALL_FRIENDS_QUERY = "SELECT u.* " +
            "FROM users u " +
            "JOIN friendship_statuses fs ON u.user_id = fs.friend_id WHERE fs.user_id = ?";
    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT u.* " +
            "FROM users u " +
            "JOIN friendship_statuses fs ON u.user_id = fs.friend_id " +
            "JOIN friendship_statuses fs2 ON u.user_id = fs2.friend_id " +
            "WHERE fs.user_id = ? AND fs2.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userRowMapper") RowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public User findUser(long id) {
        return findOne(GET_ONE_USER_QUERY, id);
    }

    @Override
    public List<User> findAllUsers() {
        return findMany(GET_ALL_USERS_QUERY);
    }

    @Override
    public User createUser(User user) {
        Long id = createLong(INSERT_USER_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        update(UPDATE_USER_QUERY,
                newUser.getName(),
                newUser.getLogin(),
                newUser.getEmail(),
                java.sql.Date.valueOf(newUser.getBirthday()),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

        if (getAllFriends(userId).contains(friendId)) {
            update(UPDATE_FRIEND_QUERY, userId, friendId);
        } else {
            update(INSERT_FRIEND_QUERY, userId, friendId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        if (getAllFriends(friendId).contains(userId)) {
            update(UPDATE_FRIEND_QUERY_WHEN_DELETE, friendId, userId);
            update(DELETE_FRIEND_QUERY, userId, friendId);
        } else {
            update(DELETE_FRIEND_QUERY, userId, friendId);
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        return findMany(GET_ALL_FRIENDS_QUERY, id);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        return findMany(GET_COMMON_FRIENDS_QUERY, userId, otherId);
    }
}
