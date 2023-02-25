package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("ReviewDbStorage")
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FeedDao feedDao;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage, FilmDbStorage filmDbStorage, FeedDao feedDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.feedDao = feedDao;
    }

    @Override
    public Review create(Review review) throws ValidationException {
        if(review.getContent().isBlank()) {
            log.debug("Возникла ошибка при валдиации объекта: {}", review);
            throw new ValidationException("отзыв не может содержать только пробелы!");
        }

        reviewValidation(review);

        String sqlQuery = "insert into REVIEWS(content, isPositive, user_Id, film_Id) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt =
                    connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());

            return stmt;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().intValue());

        feedDao.addToUserFeed(review.getUserId(), review.getReviewId(), FeedEventType.REVIEW, FeedOperation.ADD);

        log.debug("Создан объект: {}", review);

        return review;
    }

    @Override
    public Review update(Review review) throws ValidationException {
        if(review.getContent().isBlank()) {
            log.debug("Возникла ошибка при валдиации объекта: {}", review);
            throw new ValidationException("отзыв не может содержать только пробелы!");
        }

       /* String sqlQuery = "update REVIEWS set content = ?, isPositive = ?, " +
                "user_Id = ?, film_Id = ? where id = ?";*/
        String sqlQuery = "update REVIEWS set content = ?, isPositive = ? " +
                "where id = ?";

        if(jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
              //  review.getUserId(),
              //  review.getFilmId(),
                review.getReviewId()) !=1){
            throw new ObjectNotFoundException("Невозможно обновить данные объекта с id = " + review.getReviewId() +
                    ", такого объекта не сущесвтует");
        }
        reviewValidation(review);

        Review updatedReview = get(review.getReviewId());
        feedDao.addToUserFeed(updatedReview.getUserId(), updatedReview.getReviewId(), FeedEventType.REVIEW, FeedOperation.UPDATE);

        log.info("Обзор с id = {} успешно обновлен", updatedReview.getReviewId());
        return updatedReview;
    }

    @Override
    public List<Review> findAll() {
        String sqlQuery = "select * from Reviews";
        List<Review> reviewList = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs));
        HashMap<Integer,Integer> useful = getUsefulByReviewIdList(reviewList.stream().map(Review::getReviewId).collect(Collectors.toList()));
        reviewList.forEach(review -> review.setUseful(useful.get(review.getReviewId())));
        reviewList.sort(Comparator.comparing(Review::getUseful).reversed());
        return reviewList;
    }

    @Override
    public Review delete(Review review) {
        String sqlQuery = "delete from Reviews where id = ?";
        jdbcTemplate.update(sqlQuery, review.getReviewId());
        feedDao.addToUserFeed(review.getUserId(), review.getReviewId(), FeedEventType.REVIEW, FeedOperation.REMOVE);

        return review;
    }

    @Override
    public Review get(int id) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select * from Reviews where id = ?", id);

        HashMap<Integer,Integer> useful = getUsefulByReviewIdList(List.of(id));

        if(reviewRows.next()) {
            Review review = Review.builder().reviewId(reviewRows.getInt("id"))
                    .content(reviewRows.getString("content"))
                    .isPositive(reviewRows.getBoolean("isPositive"))
                    .userId(reviewRows.getInt("user_Id"))
                    .filmId(reviewRows.getInt("film_Id"))
                    .build();

            review.setUseful((useful.get(id)));

            log.info("Найден отзыв: {} ", review.getReviewId());

            return review;
        } else {
            throw new ObjectNotFoundException("Отзыв с идентификатором id = " + id
                    + "не найден.");
        }
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder().reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("isPositive"))
                .userId(rs.getInt("user_Id"))
                .filmId(rs.getInt("film_Id")).build();
    }

    private HashMap<Integer,Integer> getUsefulByReviewIdList(List<Integer> reviewIdList) {
        String parameters = String.join(",", Collections.nCopies(reviewIdList.size(), "?"));
        String queryText = "select review_id, SUM(useful) as useful from Review_usefuls " +
                            "where review_id IN (%s) GROUP BY review_id";
        String usefulQuery = String.format(queryText,parameters);
        SqlRowSet usefulRows = jdbcTemplate.queryForRowSet(usefulQuery, reviewIdList.toArray());

        HashMap<Integer,Integer> reviewUseful = new HashMap<>();
        reviewIdList.forEach(reviewId -> reviewUseful.put(reviewId,0));
        while (usefulRows.next()) {
            reviewUseful.put(usefulRows.getInt("review_id"),usefulRows.getInt("useful"));
        }
        return reviewUseful;
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer filmId, Integer reviewsCount){
        String sqlQuery = "select * from Reviews where film_id = ?";
        List<Review> reviewList = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs), filmId);
        HashMap<Integer,Integer> useful = getUsefulByReviewIdList(reviewList.stream().map(Review::getReviewId).collect(Collectors.toList()));
        reviewList.forEach(review -> review.setUseful(useful.get(review.getReviewId())));
        reviewList.sort(Comparator.comparing(Review::getUseful).reversed());
        return reviewList.stream().limit(reviewsCount).collect(Collectors.toList());
    }

    private  void  reviewValidation(Review review){
        if(review.getUserId() < 0){
            throw new ObjectNotFoundException("Пользователя не существует !");
        }
        if(review.getFilmId() < 0){
            throw new ObjectNotFoundException("Фильма не существует !");
        }
        if(review.getUserId() == 0){
            throw new IncorrectParameterException("Пользователя не существует !");
        }
        if(review.getFilmId() == 0){
            throw new IncorrectParameterException("Фильма не существует !");
        }

        userDbStorage.get(review.getUserId());
        filmDbStorage.get(review.getFilmId());
    }

}
