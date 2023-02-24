package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;


@Component
@Slf4j
public class GenreDaoImplementation implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenre() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Collection<Genre> getAllFilmGenre(Integer id) {
        String sql = "select * from film_genre where film_id = ? ORDER BY GENRE_ID";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs), id));
    }

    @Override
    public Genre getGenreById(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from genres where id = ?", id);

        if(mpaRows.next()) {
            Genre genre = Genre.builder()
                    .id(mpaRows.getInt("id"))
                    .name(mpaRows.getString("name"))
                    .build();

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            throw new ObjectNotFoundException("Жанр с id = " + id + " не найден.");
        }
    }

    @Override
    public void createFilmGenre(Integer filmId, Genre genre) {
        String sqlQuery = "insert into Film_genre (film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genre.getId());
    }

    @Override
    public void deleteFilmGenre(Integer id) {
        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Genre makeFilmGenre(ResultSet rs) throws SQLException {
        FilmGenre filmGenre = FilmGenre.builder()
                .film_id(rs.getInt("film_id"))
                .genre_id(rs.getInt("genre_id"))
                .build();

        return getGenreById(filmGenre.getGenre_id());
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Genre(id, name);
    }

}
