package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;

@Repository
public class MpaRatingDbStorage extends BaseRepository<MpaRating> implements MpaRatingStorage {

    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM ratings ORDER BY rating_id;";
    private static final String GET_MPA_ID_QUERY = "SELECT * FROM ratings WHERE rating_id = ?;";

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate, RowMapper<MpaRating> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        return findMany(GET_ALL_MPA_QUERY);
    }

    @Override
    public MpaRating getMpaRating(Integer id) {
        return (MpaRating) findOne(GET_MPA_ID_QUERY, id);
    }
}
