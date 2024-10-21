package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundExceptions;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatingStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRating(int id) {
        MpaRating mpaRating = mpaRatingStorage.getMpaRating(id);
        if (mpaRating == null) {
            log.error("Возрастное ограничение с ID {} не найдено", id);
            throw new NotFoundExceptions(String.format("Возрастное ограничение с ID {} не найдено", id));
        }
        return mpaRating;
    }
}
