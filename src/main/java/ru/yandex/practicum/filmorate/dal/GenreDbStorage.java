package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String GET_ALL_GENRE_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String GET_GENRE_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return findMany(GET_ALL_GENRE_QUERY);
    }

    @Override
    public Genre getGenre(Integer id) {
        return findOne(GET_GENRE_ID_QUERY, id);
    }
}
