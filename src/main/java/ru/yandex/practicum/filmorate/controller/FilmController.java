package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        log.info("Выполнение запроса на получение всех фильмов");
        return filmService.findAllFilms();
    }

    @GetMapping(value = "/{id}")
    public Film findFilm(@PathVariable Long id) {
        log.info("Выполнение запроса на получение фильма с ID {}", id);
        return filmService.findFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        log.info("Выполнение запроса на создание нового фильма " + newFilm.getName());
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Выполнение запроса на обновление фильма " + newFilm.getName());
        return filmService.updateFilm(newFilm);
    }

    @PutMapping(value = "{filmId}/like/{userId}")
    public Film setLikeToFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
        return filmService.setLikeToFilm(filmId, userId);
    }

    @DeleteMapping(value = "{filmId}/like/{userId}")
    public Film removeLikeToFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пользователь с ID {} удалил лайк к фильму с ID {}", userId, filmId);
        return filmService.removeLikeToFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Выполнение запроса на получение {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}