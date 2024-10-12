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
        film.setDuration((long) -1);
        assertThrows(ValidationExceptions.class, () -> filmController.createFilm(film));
    }
}