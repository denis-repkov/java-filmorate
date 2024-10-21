package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("filmStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String INSERT_FILM_QUERY = "INSERT INTO films " +
            "(name, description, releaseDate, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, " +
            "description = ?, " +
            "releaseDate = ?, " +
            "duration = ?, " +
            "mpa_rating_id = ?  " +
            "WHERE film_id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT f.*," +
            "fl.liked_user_id," +
            "fl.film_id liked_film_id," +
            "r.rating_name," +
            "fg.genre_id genre_id," +
            "g.genre_name" +
            "FROM films f " +
            "LEFT JOIN film_likes fl on f.film_id = fl.film_id " +
            "LEFT JOIN ratings r on f.mpa_rating_id = r.rating_id " +
            "LEFT JOIN films_genres fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g on fg.genre_id = g.genre_id ";

    private static final String GET_FILM_QUERY = "SELECT f.*, " +
            "fl.liked_user_id," +
            "fl.film_id liked_film_id," +
            "r.rating_name," +
            "fg.genre_id genre_id," +
            "g.genre_name" +
            "FROM films f " +
            "LEFT JOIN film_likes fl on f.film_id = fl.film_id " +
            "LEFT JOIN ratings r on f.mpa_rating_id = r.rating_id " +
            "LEFT JOIN films_genres fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g on fg.genre_id = g.genre_id " +
            "WHERE f.film_id = ?";
    private static final String INSERT_GENRE_FILM_QUERY = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_likes (film_id, liked_user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND liked_user_id = ?";
    private static final String GET_TOP_FILMS_QUERY = "SELECT f.*, " +
            "r.rating_name, " +
            "fg.genre_id, " +
            "g.genre_name, " +
            "COUNT(fl.liked_user_id) AS count_like " +
            "FROM films f " +
            "LEFT JOIN film_likes fl on f.film_id = fl.film_id " +
            "LEFT JOIN ratings r on f.mpa_rating_id = r.rating_id " +
            "LEFT JOIN films_genres fg ON f.film_id = fg.film_id " +
            "LEFT JOIN genre g on fg.genre_id = g.genre_id " +
            "GROUP BY f.film_id, r.rating_name, fg.genre_id, g.genre_name " +
            "ORDER BY count_like DESC";
    private static final String DELETE_GENRE_TO_FILM_QUERY = "DELETE FROM films_genres WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmRowMapper") RowMapper mapper,
                         ResultSetExtractor<List<Film>> extractor) {
        super(jdbcTemplate, mapper, extractor);
    }

    @Override
    public Film findFilm(long id) {
        return (Film) findOneExtr(GET_FILM_QUERY, id);
    }

    @Override
    public List<Film> findAllFilms() {
        return findMany(GET_ALL_FILMS_QUERY);
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        String query = GET_TOP_FILMS_QUERY;

        if (count != null) {
            query += " LIMIT " + count;
        }
        if (count == null || count < findAllFilms().size()) {
            return findMany(query);
        }
        return findMany(GET_TOP_FILMS_QUERY);
    }

    @Override
    public Film createFilm(Film film) {
        Long id = createLong(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().toString(),
                film.getDuration(),
                film.getMpaRating().getId()
        );
        film.setId(id);

        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            batchArgs.add(new Object[]{film.getId(), genre.getId()});
        }

        batchUpdate(INSERT_GENRE_FILM_QUERY, batchArgs);

        return film;
    }

    public Film updateFilm(Film newFilm) {
        update(UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpaRating().getId(),
                newFilm.getId());

        deleteAllGenreToFilm(newFilm.getId());

        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : newFilm.getGenres()) {
            batchArgs.add(new Object[]{newFilm.getId(), genre.getId()});
        }

        batchUpdate(INSERT_GENRE_FILM_QUERY, batchArgs);

        return newFilm;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        update(INSERT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteAllGenreToFilm(Long filmId) {
        delete(DELETE_GENRE_TO_FILM_QUERY, filmId);
    }
}
