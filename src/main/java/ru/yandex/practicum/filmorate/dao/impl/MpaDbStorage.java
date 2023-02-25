package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class MpaDbStorage implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "select * from mpa";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> getMpaById(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", id);

        if(mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(mpaRows.getInt("id"))
                    .name(mpaRows.getString("name"))
                    .build();

            log.info("Найден рейтин: {} {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } else {
            throw new ObjectNotFoundException("Рейтинг с id = " + id + " не найден.");
        }
    }

    @Override
    public Optional<Mpa> createFilmMpa(Film film, Integer id) {
        return Optional.empty();
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        return new Mpa(id, name);
    }
}
