package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.AbstractStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage extends AbstractStorage<Film> implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final DirectorDao directorDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         GenreDao genreDao,
                         MpaDao mpaDao,
                         DirectorDao directorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.directorDao = directorDao;
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

        directorDao.deleteFilmDirector(film.getId());
        Set<Director> filmDirectors = new HashSet<>(film.getDirectors());
        if(!filmDirectors.isEmpty()){
            for(Director director :filmDirectors){
                directorDao.createFilmDirector(film.getId(), director);
            }
        }
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

        if(film.getDirectors() == null || film.getDirectors().isEmpty()) {
            directorDao.deleteFilmDirector(film.getId());
        } else {
            directorDao.deleteFilmDirector(film.getId());
            Set<Director> filmDirector = new HashSet<>(film.getDirectors());
            if(!filmDirector.isEmpty()){
                for(Director director:filmDirector){
                    directorDao.createFilmDirector(film.getId(), director);
                }
            }
        }
        if (film.getGenres() == null || film.getGenres().isEmpty()){
            genreDao.deleteFilmGenre(film.getId());
        }  else {
            genreDao.deleteFilmGenre(film.getId());
            Set<Genre> filmGenres = new HashSet<>(film.getGenres());
            if(!filmGenres.isEmpty()){
                for(Genre genre:filmGenres){
                    genreDao.createFilmGenre(film.getId(), genre);
                }
            }
        }
        log.info("Фильм с id = {} успешно обновлен", film.getId());
        return get(film.getId());
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
            film.setDirectors((Set<Director>) directorDao.getAllFilmDirector(film.getId()));

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
    
    @Override
    public List<Film> getDirectorFilmsByYear(Integer id) {
        Long i = Long.valueOf(id);
        String sqlQuery = "select f.* " +
                "from films_directors " +
                "join FILMS f on f.ID = films_directors.film_id " +
                "where director_id = " + i +
                " group by f.id " +
                "order by extract(year from f.release_date)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> createFilm(rs));
    }

    @Override
    public List<Film> getDirectorFilmsByRating(Integer id) {
        String sqlQuery = "select f.* from likes " +
                "RIGHT JOIN FILMS F on F.ID = likes.FILM_ID " +
                "JOIN FILMS_DIRECTORS FD on F.ID = FD.FILM_ID where DIRECTOR_ID = ? " +
                "group by f.ID order by count(likes.USER_ID)";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> createFilm(rs), id);
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
            film.setDirectors((Set<Director>) directorDao.getAllFilmDirector(film.getId()));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return film;
    }

    @Override
    public List<Film> getFilmsByIds(List<Integer> filmsIds) {
        if (filmsIds.isEmpty()) return new ArrayList<>();
        String inSql = String.join(",", Collections.nCopies(filmsIds.size(), "?"));
        String filmsQuery = "select * from films " +
                "where id in (" + inSql + ")";
        return jdbcTemplate.query(filmsQuery, (rs, rowNum) -> createFilm(rs), filmsIds.toArray());
    }

    public List<Film> search (String query, boolean isTitle, boolean isDirector) {
        String lQuery = query.toLowerCase();
        if (!isDirector && !isTitle ){
            throw new ObjectNotFoundException("Wrong params value");
        }

        if (isDirector && !isTitle){
            String sqlReq = "SELECT " +
                    "f.*" +
                    "FROM films as f " +
                    "LEFT JOIN films_directors as fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors as d ON fd.DIRECTOR_ID = d.id " +
                    "LEFT JOIN (SELECT " +
                    "film_id, " +
                    "COUNT(user_id) as cl " +
                    "FROM likes " +
                    "GROUP BY film_id) as l ON f.id = l.film_id " +
                    "WHERE LOWER (d.name) LIKE CONCAT( '%',?,'%') " +
                    "GROUP BY f.id " +
                    "ORDER BY cl DESC";
            return jdbcTemplate.query(sqlReq, (rs, rowNum) -> createFilm(rs), lQuery);
        } else if (isTitle && !isDirector) {
            String sqlReq = "SELECT " +
                    "f.* " +
                    "FROM films AS f " +
                    "LEFT JOIN (SELECT " +
                    "film_id, " +
                    "COUNT(user_id) as cl " +
                    "FROM likes AS lq " +
                    "GROUP BY film_id) AS l ON f.id =  l.film_id " +
                    "WHERE LOWER (name) LIKE CONCAT('%',?,'%') " +
                    "GROUP BY f.id " +
                    "ORDER BY cl DESC";
            return jdbcTemplate.query(sqlReq, (rs, rowNum) -> createFilm(rs), lQuery);// find by title
        } else {
            String sqlReq = "SELECT " +
                    "f.* " +
                    "FROM films AS f " +
                    "LEFT JOIN (SELECT " +
                    "film_id, " +
                    "COUNT(user_id) as cl " +
                    "FROM likes as lq " +
                    "GROUP BY film_id) AS l ON f.id = l.film_id " +
                    "LEFT JOIN films_directors as fd ON f.id = fd.film_id " +
                    "LEFT JOIN directors as d ON fd.DIRECTOR_ID = d.id " +
                    "WHERE LOWER (f.name) LIKE CONCAT ('%', ?, '%') OR LOWER(d.name) LIKE CONCAT ('%',?,'%') " +
                    "ORDER BY cl DESC";
            return jdbcTemplate.query(sqlReq, (rs, rowNum) -> createFilm(rs), lQuery, lQuery); // full search
        }
    }
}
