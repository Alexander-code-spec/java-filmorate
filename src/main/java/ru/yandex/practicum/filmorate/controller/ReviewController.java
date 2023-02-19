package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    @ResponseBody
    public Review getReviewById(@PathVariable("id") Integer id) {
        if(reviewService.getReviewStorage().get(id) == null){
            throw new ObjectNotFoundException("Отзыва не существует!");
        }
        return reviewService.getReviewStorage().get(id);
    }

    @PostMapping
    @Validated
    public Review create(@RequestBody @Valid Review review) throws ValidationException {
        return reviewService.getReviewStorage().create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) throws ValidationException {
        return reviewService.getReviewStorage().update(review);
    }

    @DeleteMapping
    public Boolean deleteReview(@Valid @RequestBody Review review){
        if(reviewService.getReviewStorage().delete(review) == null){
            throw new ObjectNotFoundException("Отзыв не существует!");
        }
        return true;
    }

    @DeleteMapping("/{reviewId}")
    public Boolean deleteReviewById(@PathVariable("reviewId") Integer reviewId){
        if(reviewService.getReviewStorage().get(reviewId) == null){
            throw new ObjectNotFoundException("Отзыв не существует!");
        }
        else {
            reviewService.getReviewStorage().delete(reviewService.getReviewStorage().get(reviewId));
        }
        return true;
    }

    @GetMapping
    @ResponseBody
    public List<Review> getReviewByFilmId(@RequestParam(defaultValue = "-1") Integer filmId, @RequestParam(defaultValue = "10") Integer count) {
        if(filmId == -1){
            return reviewService.getReviewStorage().findAll();
        }
        return reviewService.getReviewStorage().getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable("id") Integer id, @PathVariable() Integer userId) {
        reviewService.putLike(id, userId, 1);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        reviewService.removeLike(id, userId, 1);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDisLike(@PathVariable("id") Integer id, @PathVariable() Integer userId) {
        reviewService.putLike(id, userId, -1);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        reviewService.removeLike(id, userId, -1);
    }

}
