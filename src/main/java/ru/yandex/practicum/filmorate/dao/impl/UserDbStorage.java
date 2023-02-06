package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("UserDbStorage")
@Slf4j
public class UserDbStorage  implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) throws ValidationException {
        if(user.getLogin().contains(" ")) {
            log.debug("Возникла ошибка при валдиации объекта: {}", user);
            throw new ValidationException("Логин не должен содержать пробелы!");
        } else if(user.getName().isEmpty()){
            user.setName(user.getLogin());
        }
        String sqlQuery = "insert into Users(name, email, login, birthdate) " +
                "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt =
                    connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));

            return stmt;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());
        log.debug("Создан объект: {}", user);

        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        if(user.getLogin().contains(" ")) {
            log.debug("Возникла ошибка при валдиации объекта: {}", user);
            throw new ValidationException("Логин не должен содержать пробелы!");
        }

        String sqlQuery = "update users set name = ?, email = ?, " +
                "login = ?, birthdate = ? where id = ?";

        if(jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()) !=1){
            throw new ObjectNotFoundException("Невозможно обновить данные объекта с id = " + user.getId() +
                    ", такого объекта не сущесвтует");
        }
        log.info("Пользователь с id = {} успешно обновлен", user.getId());
        return user;
    }

    @Override
    public User delete(User user) {
        String sqlQuery = "delete from users where id = ? cascade";
        jdbcTemplate.update(sqlQuery, user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User get(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from Users where id = ?", id);

        if(userRows.next()) {
            User user = User.builder().id(userRows.getInt("id"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .email(userRows.getString("email"))
                    .birthday(userRows.getDate("birthdate").toLocalDate()).build();

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());

            return user;
        } else {
            throw new ObjectNotFoundException("Пользователь с идентификатором id = " + id
                    + "не найден.");
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder().id(rs.getInt("id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthdate").toLocalDate()).build();
    }
}
