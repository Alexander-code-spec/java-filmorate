package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private int id;
    @NotBlank(message = "Необходимо указать название")
    private String name;
    @Length(message = "Длинна описания не может превышать 200 симыолов",max = 200)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Отрицательная продолжительность")
    private int duration;
    private Mpa mpa;
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
