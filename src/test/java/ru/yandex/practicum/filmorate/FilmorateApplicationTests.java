package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmDbStorage.class, FilmRowMapper.class, FilmExtractor.class, FilmService.class, MpaRatingService.class,
        MpaRatingDbStorage.class, RatingRowMapper.class, GenreRowMapper.class, GenreService.class, GenreDbStorage.class,
        UserService.class, UserDbStorage.class, UserRowMapper.class})
@DisplayName("Интеграционное тестирование приложения")
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        firstUser = new User();
        firstUser.setName("userOne");
        firstUser.setLogin("user1");
        firstUser.setEmail("someone1@email.com");
        firstUser.setBirthday(LocalDate.of(2000, 1, 1));

        secondUser = new User();
        secondUser.setName("userTwo");
        secondUser.setLogin("user2");
        secondUser.setEmail("someone2@email.com");
        secondUser.setBirthday(LocalDate.of(2001, 2, 3));

        thirdUser = new User();
        thirdUser.setId(firstUser.getId());
        thirdUser.setName("userThree");
        thirdUser.setLogin("update3");
        thirdUser.setEmail("sm3@email.com");
        thirdUser.setBirthday(LocalDate.of(2001, 12, 30));

        firstFilm = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1950, 5, 20))
                .duration(60)
                .mpa(new MpaRating(1, "G"))
                .genres(Arrays.asList(new Genre(1, "Комедия"), new Genre(2, "Драма")))
                .likesFromUserIds(Set.of())
                .build();

        secondFilm = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1960, 6, 2))
                .duration(70)
                .mpa(new MpaRating(2, "PG"))
                .genres(Arrays.asList(new Genre(1, "Комедия"), new Genre(2, "Драма")))
                .likesFromUserIds(Set.of())
                .build();
    }

    @Test
    @DisplayName("Добавление пользователя и получение его по ID")
    void testCreateAndFindUserById() {
        userService.createUser(firstUser);
        User user = userService.findUser(firstUser.getId());
        Assertions.assertEquals(firstUser.getId(), userStorage.findUser(user.getId()).getId());
    }

    @Test
    @DisplayName("Получение списка пользователей")
    void testGetAllUsers() {
        userService.createUser(firstUser);
        userService.createUser(secondUser);
        Collection<User> users = userService.findAllUsers();
        Assertions.assertEquals(userService.findAllUsers(), users);
    }

    @Test
    @DisplayName("Обновление пользователя")
    void testUpdateUser() {
        userService.createUser(firstUser);
        User updateUser = new User();
        updateUser.setId(firstUser.getId());
        updateUser.setName("updateN");
        updateUser.setLogin("updateL");
        updateUser.setEmail("update@email.com");
        updateUser.setBirthday(LocalDate.of(2011, 12, 30));

        userService.updateUser(updateUser);
        User user = userService.findUser(updateUser.getId());
        Assertions.assertEquals(updateUser.getId(), user.getId());
    }

    @Test
    @DisplayName("Добавление фильма и получение его по ID")
    void testCreateAndFindFilmById() {
        filmStorage.createFilm(firstFilm);
        Film film = filmStorage.findFilm(firstFilm.getId());
        Assertions.assertEquals(firstFilm.getId(), film.getId());
    }

    @Test
    @DisplayName("Получение списка фильмов")
    void testGetAllFilms() {
        filmStorage.createFilm(firstFilm);
        filmStorage.createFilm(secondFilm);
        Collection<Film> films = filmStorage.findAllFilms();
        Assertions.assertEquals(filmStorage.findAllFilms(), films);
    }

    @Test
    @DisplayName("Установка фильму лайка")
    void testAddLike() {
        userStorage.createUser(firstUser);
        filmStorage.createFilm(firstFilm);
        filmStorage.addLike(firstFilm.getId(), firstUser.getId());

        Film firstFilm1 = filmStorage.findFilm(firstFilm.getId());
        Assertions.assertNotNull(firstFilm1.getLikesFromUserIds());
    }

    @Test
    @DisplayName("Получение списка популярных фильмов")
    void testGetPopular() {
        userStorage.createUser(firstUser);
        userStorage.createUser(secondUser);
        firstFilm = filmStorage.createFilm(firstFilm);
        secondFilm = filmStorage.createFilm(secondFilm);

        filmStorage.addLike(firstFilm.getId(), firstUser.getId());
        filmStorage.addLike(secondFilm.getId(), firstUser.getId());
        filmStorage.addLike(secondFilm.getId(), secondUser.getId());

        Collection<Film> popular = filmStorage.getPopularFilms(10L);

        Assertions.assertEquals(popular, filmStorage.getPopularFilms(10L));
        Assertions.assertNotNull(filmStorage.findAllFilms());
    }

    @Test
    @DisplayName("Добавление в друзья")
    void testAddFriend() {
        firstUser = userService.createUser(firstUser);
        secondUser = userService.createUser(secondUser);

        Collection<User> friends = new ArrayList<>();
        friends.add(secondUser);
        Collection<User> friend = new ArrayList<>();

        userService.addFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertNotNull(userService.getFriends(firstUser.getId()));
        Assertions.assertEquals(friends, userService.getFriends(firstUser.getId()));
        Assertions.assertEquals(friend, userService.getFriends(secondUser.getId()));
    }

    @Test
    @DisplayName("Удаление из друзей")
    void testDeleteFriend() {
        firstUser = userService.createUser(firstUser);
        secondUser = userService.createUser(secondUser);

        Collection<User> all = new ArrayList<>();

        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.removeFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(all, userService.getFriends(firstUser.getId()));
    }

    @Test
    @DisplayName("Получение списка друзей")
    void testGetFriends() {
        firstUser = userService.createUser(firstUser);
        secondUser = userService.createUser(secondUser);
        thirdUser = userService.createUser(thirdUser);

        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());

        Collection<User> users = new ArrayList<>();
        users.add(secondUser);
        users.add(thirdUser);
        Assertions.assertEquals(users, userService.getFriends(firstUser.getId()));
    }

    @Test
    @DisplayName("Получение списка общих друзей")
    void testGetCommonFriends() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        Collection<User> common = new ArrayList<>();
        User users = userService.findUser(thirdUser.getId());
        common.add(users);

        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), firstUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());

        Assertions.assertEquals(common, userService.getCommonFriends(firstUser.getId(), secondUser.getId()));
    }
}