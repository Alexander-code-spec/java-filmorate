package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewLikesDao;

@Component
@Slf4j
@Qualifier("ReviewLikesDao")
public class ReviewLikesDaoImplementation implements ReviewLikesDao {

    private JdbcTemplate jdbcTemplate;

    public ReviewLikesDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createReviewLike(Integer reviewId, Integer userId, Integer likeValue) {
        String sqlQuery = "insert into Review_usefuls (review_id, user_id, useful) values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, reviewId, userId, likeValue);
    }

    @Override
    public void deleteReviewLike(Integer reviewId, Integer userId, Integer likeValue) {
        String sql = "delete from Review_usefuls where review_id = ? and user_id = ? and useful = ?";
        jdbcTemplate.update(sql, reviewId, userId, likeValue);
    }

}
