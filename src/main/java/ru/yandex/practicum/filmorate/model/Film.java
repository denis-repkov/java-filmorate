package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;
    @Positive(message = "Длительность не может быть отрицательной")
    private long duration;
    private Set<Long> likes = new HashSet<>();
}
