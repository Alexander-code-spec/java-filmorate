package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FilmDirectors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;


@Component
@Slf4j
public class DirectorsDbStorage implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Director> getDirectorsList() {
        String sql = "select * from directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(Integer id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("select * from directors where id = ?", id);
        log.info("Запрос на получение режиссера по id.");
        if(directorRows.next()) {
            Director director = Director.builder()
                    .id(directorRows.getInt("id"))
                    .name(directorRows.getString("name"))
                    .build();

            log.info("Найден режиссер" +
                    ": {} {}", director.getId(), director.getName());

            return director;
        } else {
            throw new ObjectNotFoundException("Режиссер с идентификатором id = " + id
                    + "не найден.");
        }
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Обновление режиссера с id = {}", director.getId());
        String sqlQuery = "update directors set name = ? where id = ? ";

        if(jdbcTemplate.update(sqlQuery, director.getName(),
                director.getId()) != 1){
            throw new ObjectNotFoundException("Такой режиссер не существует");
        }
        return director;
    }

    @Override
    public Director createDirector(Director director) {
        String sqlQuery = "insert into directors(name) " +
                "values (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt =
                    connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());

            return stmt;
        }, keyHolder);

        director.setId(keyHolder.getKey().intValue());
        log.debug("Создан объект: {}", director);

        return director;
    }

    @Override
    public void deleteDirector(Integer id) {
        if (getDirectorById(id) != null) {
            jdbcTemplate.update("delete from films_directors" +
                    " where director_id = ?", id);
            String sql = "delete from directors where id = ?";
            jdbcTemplate.update(sql, id);

        } else {
            throw new ObjectNotFoundException("Режиссера с id = " + id + " не существует");
        }
    }

    @Override
    public void createFilmDirector(Integer filmId, Director director) {
        String sqlQuery = "insert into films_directors (film_id, director_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, director.getId());
    }

    @Override
    public void deleteFilmDirector(Integer filmId) {
        String sqlQuery = "delete from films_directors where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Collection<Director> getAllFilmDirector(Integer filmId) {
        String sql = "select * from films_directors where film_id = " + filmId;
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmDirectors(rs)));
    }

    private Director makeFilmDirectors(ResultSet rs) throws SQLException {
        FilmDirectors filmDirector = FilmDirectors.builder()
                .film_id(rs.getInt("film_id"))
                .director_id(rs.getInt("director_id"))
                .build();

        return getDirectorById(filmDirector.getDirector_id());
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Director(id, name);
    }
}
