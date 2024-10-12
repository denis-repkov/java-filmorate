package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film findFilm(long id) {
        return filmStorage.findFilm(id);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film createFilm(Film newFilm) {
        filmValidation(newFilm);
        return filmStorage.createFilm(newFilm);
    }

    public Film updateFilm(Film newFilm) {
        filmValidation(newFilm);
        return filmStorage.updateFilm(newFilm);
    }

    public Film setLikeToFilm(long filmId, long userId) {
        userStorage.findUser(userId);
        Film film = findFilm(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLikeToFilm(long filmId, long userId) {
        userStorage.findUser(userId);
        Film film = findFilm(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return findAllFilms().stream()
                .filter(film -> film.getLikes() != null)
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void filmValidation(Film newFilm) {
        if (newFilm == null) {
            log.error("Попытка создания нового фильма с пустым объектом");
            throw new ValidationExceptions("Необходимо заполнить все данные");
        }
        if (newFilm.getName() == null || newFilm.getName().isEmpty()) {
            log.error("Пользователь попытался создать новый фильм с пустым названием");
            throw new ValidationExceptions("Название фильма не должно быть пустым");
        }
        if (newFilm.getDescription().isEmpty() || newFilm.getDescription().length() > 200) {
            log.error("Пользователь попытался создать новый фильм с пустым описанием или длинной более 200 символов");
            throw new ValidationExceptions("Введенное описание должно быть не более 200 символов");
        }
        if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Пользователь попытался создать новый фильм с датой релиза раньше 28.12.1895 года");
            throw new ValidationExceptions("Дата выxода фильма не может быть раньше 28.12.1895 года");
        }
        if (newFilm.getDuration() <= 0) {
            log.error("Пользователь попытался создать новый фильм с длительностью меньше 1 минуты");
            throw new ValidationExceptions("Длительность не может быть меньше 1 минуты");
        }
    }
}
