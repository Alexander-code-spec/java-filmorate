package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.ReviewLikesDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikesDao reviewLikesDao;
    private final UserStorage userStorage;
    private final FeedDao feedDao;

    @Autowired
    public ReviewService(@Qualifier("ReviewDbStorage") ReviewStorage reviewStorage,
                         @Qualifier("ReviewLikesDao") ReviewLikesDao reviewLikesDao,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         FeedDao feedDao){
        this.reviewStorage = reviewStorage;
        this.reviewLikesDao = reviewLikesDao;
        this.userStorage = userStorage;
        this.feedDao = feedDao;
    }

    public void putLike(Integer reviewId, Integer userId, Integer likeValue) {
        if(userId < 0){
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        }
        userStorage.get(userId);
        reviewStorage.get(reviewId);
        reviewLikesDao.createReviewLike(reviewId, userId, likeValue);
    }

    public void removeLike(Integer reviewId, Integer userId, Integer likeValue){
        if(userId < 0){
            throw new ObjectNotFoundException("id не может быть меньше 0!");
        }
        userStorage.get(userId);
        reviewStorage.get(reviewId);

        reviewLikesDao.deleteReviewLike(reviewId, userId, likeValue);
    }

    public ReviewStorage getReviewStorage() {
        return reviewStorage;
    }
}
