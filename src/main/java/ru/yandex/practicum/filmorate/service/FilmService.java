package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.NotFoundExceptions;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmStorage")FilmStorage filmStorage,
                       @Qualifier("userStorage")UserStorage userStorage,
                       MpaRatingStorage mpaRatingStorage,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaRatingStorage = mpaRatingStorage;
        this.genreStorage = genreStorage;
    }

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
        if (filmStorage.findFilm(newFilm.getId()) == null) {
            throw new NotFoundExceptions("Не удалось обновить данные - фильм не найден!");
        }
        filmValidation(newFilm);
        return filmStorage.updateFilm(newFilm);
    }

    public Film setLikeToFilm(long filmId, long userId) {
        userStorage.findUser(userId);
        Film film = findFilm(filmId);
        filmStorage.addLike(filmId, userId);
        return film;
    }

    public Film removeLikeToFilm(long filmId, long userId) {
        userStorage.findUser(userId);
        Film film = findFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
        return film;
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
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
        if (newFilm.getDescription() == null || newFilm.getDescription().isEmpty() || newFilm.getDescription().length() > 200) {
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
        if (newFilm.getMpaRating() != null && !isIdMpa(newFilm.getMpaRating())) {
            log.error("Указанный ID возрастного рейтинга отсутствует");
            throw new ValidationExceptions("Указанный ID возрастного рейтинга отсутствует");
        }
        if (newFilm.getGenres() == null) {
            newFilm.setGenres(new ArrayList<>());
        } else {
            validateGenres(newFilm);
        }
    }

    private boolean isIdMpa(MpaRating mpaRating) {
        boolean isId = false;
        Collection<MpaRating> allMpa = mpaRatingStorage.getAllMpaRatings();
        for (MpaRating allMpaRat : allMpa) {
            if (Objects.equals(allMpaRat.getId(), mpaRating.getId())) {
                isId = true;
                break;
            }
        }
        return isId;
    }

    private void validateGenres(Film film) {
        Collection<Genre> allGenres = genreStorage.getAllGenres();
        List<Integer> existingGenreIds = allGenres.stream().map(Genre::getId).toList();

        List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).toList();
        for (Integer genreId : filmGenreIds) {
            if (!existingGenreIds.contains(genreId)) {
                String errorMessage = "Жанр с ID " + genreId + " не существует";
                log.error(errorMessage);
                throw new ValidationExceptions(errorMessage);
            }
        }
    }
}
