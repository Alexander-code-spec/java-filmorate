package ru.yandex.practicum.filmorate.dao;

public interface ReviewLikesDao {

    void createReviewLike(Integer reviewId, Integer userId, Integer likeValue);

    void deleteReviewLike(Integer reviewId, Integer userId, Integer likeValue);

    //List<Integer> getUserReviewLikes(Integer reviewId, Integer userId, Integer likeValue);

}
