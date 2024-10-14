package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование контроллера фильмов")
class FilmControllerTest {
    private FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120L);
    }

    @Test
    @DisplayName("Если имя фильма пустое - получаем ошибку")
    void ifNameEmptyError() {
        film.setName("");
        assertThrows(ValidationExceptions.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Если описание фильма пустое - получаем ошибку")
    void ifDescriptionEmptyError() {
        film.setDescription("");
        assertThrows(ValidationExceptions.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Если описание фильма более 200 символов - получаем ошибку")
    void ifDescriptionMoreThan200Error() {
        film.setDescription("a".repeat(201));
        assertThrows(ValidationExceptions.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Если дата фильма раньше 28.12.1985 - получаем ошибку")
    void ifReleaseDateBefore28121985Error() {
        film.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ValidationExceptions.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Если длительность фильма меньше 0 - получаем ошибку")
    void ifDurationLessThan0Error() {
        film.setDuration(-1L);
        assertThrows(ValidationExceptions.class, () -> filmController.createFilm(film));
    }

    @Test
    @DisplayName("Получение всех фильмов")
    void findAllFilms() {
        filmController.createFilm(film);
        List<Film> films = filmController.findAllFilms();
        assertEquals(1, films.size());
        assertEquals(film.getName(), films.get(0).getName());
    }

    @Test
    @DisplayName("Получение фильма по ID")
    void findFilmById() {
        filmController.createFilm(film);
        Film foundFilm = filmController.findFilm(film.getId());
        assertEquals(film.getName(), foundFilm.getName());
    }

    @Test
    @DisplayName("Обновление фильма")
    void updateFilm() {
        filmController.createFilm(film);
        film.setName("Update Name");
        Film updatedFilm = filmController.updateFilm(film);
        assertEquals("Update Name", updatedFilm.getName());
    }

    @Test
    @DisplayName("Получение популярных фильмов")
    void getPopularFilms() {
        filmController.createFilm(film);
        List<Film> popularFilms = filmController.getPopularFilms(10);
        assertEquals(1, popularFilms.size());
        assertEquals(film.getName(), popularFilms.get(0).getName());
    }
}