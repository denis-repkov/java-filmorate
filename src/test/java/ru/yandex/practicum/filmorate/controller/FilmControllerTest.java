package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование контроллера фильмов")
class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder().name("name").description("description").releaseDate(LocalDate.now()).build();
    }

    @Test
    @DisplayName("Если имя фильма пустое - получаем ошибку")
    void ifNameEmptyError() {
        film.setName("");
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    @DisplayName("Если описание фильма пустое - получаем ошибку")
    void ifDescriptionEmptyError() {
        film.setDescription("");
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    @DisplayName("Если описание фильма более 200 символов - получаем ошибку")
    void ifDescriptionMoreThan200Error() {
        film.setDescription("a".repeat(201));
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    @DisplayName("Если дата фильма раньше 28.12.1985 - получаем ошибку")
    void ifReleaseDateBefore28121985Error() {
        film.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }

    @Test
    @DisplayName("Если длительность фильма меньше 0 - получаем ошибку")
    void ifDurationLessThan0Error() {
        film.setDuration((long) -1);
        assertThrows(ValidationExceptions.class, () -> filmController.create(film));
    }
}