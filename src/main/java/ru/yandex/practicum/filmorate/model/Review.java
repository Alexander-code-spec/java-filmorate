package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private int reviewId;
    @NotBlank(message = "Необходимо заполнить отзыв")
    private String content;
    private Boolean isPositive;
    private int userId;
    private int filmId;
    @Builder.Default
    private int useful = 0;
}
