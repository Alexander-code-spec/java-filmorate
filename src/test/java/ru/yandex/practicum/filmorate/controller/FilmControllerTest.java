package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;


class FilmControllerTest {
    FilmController filmController;
    static Film goodFilm;
    static Film badNameFilm;
    static Film badDateFilm;
    static Film badDurationFilm;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void before(){
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
    }

    @BeforeAll
    public static void setUp(){
        goodFilm = Film.builder().name("Terminator")
                .description("Good film")
                .duration(130)
                .releaseDate(LocalDate.of(1983, 5, 23)).build();
        badNameFilm = Film.builder().name("")
                .description("Good film")
                .duration(130)
                .releaseDate(LocalDate.of(1983, 5, 23)).build();
        badDateFilm = Film.builder().name("sds")
                .description("Good film")
                .duration(130)
                .releaseDate(LocalDate.of(1891, 5, 23)).build();
        badDurationFilm = Film.builder().name("sdsd")
                .description("Good film")
                .duration(-3)
                .releaseDate(LocalDate.of(1994, 5, 23)).build();
    }

    @Test
    void findAllTest() throws Exception {
        filmController.create(goodFilm);
        assertEquals(filmController.findAll().size(), 1, "Валидация выполнется некорректно");
    }

    @Test
    void create() throws Exception {
        filmController.create(goodFilm);
        assertEquals(filmController.findAll().size(), 1, "Валидация выполнется некорректно");
        String expectedMessage = "Объект с id = " + 1 + " уже сущесвтует";

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.create(goodFilm);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() throws Exception {
        filmController.create(goodFilm);
        assertEquals(filmController.findAll().size(), 1, "Валидация выполнется некорректно");

        filmController.update(Film.builder().id(1).name("Terminator2")
                .description("Very Good film")
                .duration(130)
                .releaseDate(LocalDate.of(1983, 5, 23)).build());

        assertEquals(filmController.getEmployeesById(1).getName(),
                "Terminator2",
                "Валидация выполнется некорректно");
        assertEquals(filmController.getEmployeesById(1).getDescription(),
                "Very Good film",
                "Валидация выполнется некорректно");
    }

    @Test
    public void filmDurationValidationTest(){
        Set<ConstraintViolation<Film>> violations = validator.validate(badDurationFilm);
        ConstraintViolation<Film> violation = violations.stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        assertEquals("Отрицательная продолжительность", violation.getMessageTemplate());
    }

    @Test
    public void filmNameValidationTest(){
        Set<ConstraintViolation<Film>> violations = validator.validate(badNameFilm);
        ConstraintViolation<Film> violation = violations.stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        assertEquals("Необходимо указать название", violation.getMessageTemplate());
    }
}