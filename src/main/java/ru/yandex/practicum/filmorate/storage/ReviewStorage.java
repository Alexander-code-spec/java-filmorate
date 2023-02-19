package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage extends Storage<Review>{

    List<Review> getReviewsByFilmId(Integer filmId, Integer reviewsCount);

}
