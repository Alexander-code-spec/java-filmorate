package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage extends AbstractStorage<Film> implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    @Override
    public Film create(Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }
        String sqlQuery = "insert into Films(name, description, release_date, duration, mpa) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt =
                    connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());

            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        genreDao.deleteFilmGenre(film.getId());
        Set<Genre> filmGenres = new HashSet<>(film.getGenres());
        if(!filmGenres.isEmpty()){
            for(Genre genre:filmGenres){
                genreDao.createFilmGenre(film.getId(), genre);
            }
        }

        film.setGenres(new HashSet<>(genreDao.getAllFilmGenre(film.getId())));

        log.debug("Создан объект: {}", film);

        return film;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Возникла ошибка при валдиации объекта: {}", film);
            throw new ValidationException("Неверно задана дата выпуска фильма!");
        }

        log.info("Обновление фильма с id = {}", film.getId());
        String sqlQuery = "update films set name = ?, description = ?, " +
                "release_date = ?, duration = ?, mpa = ? where id = ? ";

        if(jdbcTemplate.update(sqlQuery, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) != 1){
            throw new ObjectNotFoundException("Такой фильм не существует");
        }

        if (film.getGenres() == null || film.getGenres().isEmpty()){
            genreDao.deleteFilmGenre(film.getId());
            log.info("Фильм с id = {} успешно обновлен", film.getId());
            return film;
        } else {
            genreDao.deleteFilmGenre(film.getId());
            Set<Genre> filmGenres = new HashSet<>(film.getGenres());
            if(!filmGenres.isEmpty()){
                for(Genre genre:filmGenres){
                    genreDao.createFilmGenre(film.getId(), genre);
                }
            }

            log.info("Фильм с id = {} успешно обновлен", film.getId());
            return get(film.getId());
        }
    }

    @Override
    public Film delete(Film film) {
        String sqlQuery = "delete from films where id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        return film;
    }

    @Override
    public List<Film> findAll() {
        String filmsQuery = "select * from Films";
        log.info("Выполняется запрос всех пользователей из базы");
        return jdbcTemplate.query(filmsQuery, (rs, rowNum) -> createFilm(rs));
    }

    @Override
    public Film get(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from Films where id = ?", id);

        if(filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(Mpa.builder().id(filmRows.getInt("mpa"))
                            .name(mpaDao.getMpaById(filmRows.getInt("mpa")).get().getName())
                            .build())
                    .build();

            film.setGenres((Set<Genre>) genreDao.getAllFilmGenre(film.getId()));

            log.info("Найден фильм" +
                    ": {} {}", film.getId(), film.getName());

            return film;
        } else {
            throw new ObjectNotFoundException("Фильм с идентификатором id = " + id
                    + "не найден.");
        }
    }

    @Override
    public List<Film> getLikesCount() {
        String sqlQuery = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRE, MPA, COUNT(DISTINCT USER_ID) AS LIKES FROM FILMS " +
                "left outer join likes on films.id = likes.film_id " +
                "group by films.id order by count(distinct likes.user_id) desc";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> createFilm(rs));
    }

    @Override
    public List<Film> getCommonMovies(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, GENRE, MPA, COUNT(DISTINCT USER_ID) AS LIKES FROM FILMS \n" +
                "LEFT OUTER JOIN LIKES ON FILMS.ID = LIKES.FILM_ID \n" +
                "WHERE FILM_ID IN ( SELECT FILM FROM (SELECT \n" +
                "L.FILM_ID AS FILM, \n" +
                "COUNT(L.USER_ID) AS USERCOUNT \n" +
                "FROM \n" +
                "LIKES AS L \n" +
                "WHERE \n" +
                "L.USER_ID IN (?, ?) \n" +
                "GROUP BY \n" +
                "FILM \n" +
                "HAVING \n" +
                "USERCOUNT = 2 )) \n" +
                "GROUP BY FILMS.ID ORDER BY COUNT(DISTINCT LIKES.USER_ID) DESC";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> createFilm(rs), userId, friendId);
    }

    private Film createFilm(ResultSet filmRows) throws SQLException {
            Film film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(Mpa.builder().id(filmRows.getInt("mpa"))
                            .name(mpaDao.getMpaById(filmRows.getInt("mpa")).get().getName())
                            .build())
                    .build();

            film.setGenres((Set<Genre>) genreDao.getAllFilmGenre(film.getId()));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return film;
    }


}
