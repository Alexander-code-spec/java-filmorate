package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;
    static User goodUser;
    static User badLoginUser;
    static User badEmailUser;
    static User badBirthdateUser;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    public void before(){
        userController = new UserController(new UserService(new InMemoryUserStorage(), null, null, null, null));
    }

    @BeforeAll
    public static void setUp(){
        goodUser = User.builder().login("Sasha")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20)).build();
        badLoginUser = User.builder().login("Sasha asdsad")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20)).build();
        badEmailUser = User.builder().login("Sasha")
                .name("Nick Name")
                .email("m@sadail@@ru.mail")
                .birthday(LocalDate.of(1946, 8, 20)).build();
        badBirthdateUser = User.builder().login("Sasha")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(2024, 8, 20)).build();
    }

    @Test
    void findAll() throws ValidationException {
        userController.create(goodUser);
        assertEquals(userController.findAll().size(), 1, "Валидация выполнется некорректно");
    }

    @Test
    void create() throws ValidationException {
        String expectedMessage = "Логин не должен содержать пробелы!";

        userController.create(goodUser);
        assertEquals(userController.findAll().size(), 1, "Валидация выполнется некорректно");

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(badLoginUser);
        });
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void update() throws ValidationException {
        userController.create(goodUser);
        assertEquals(userController.findAll().size(), 1, "Валидация выполнется некорректно");

        userController.update(User.builder().id(1).name("Alexey")
                .email("asdfgqwe@gmail.com")
                .birthday(LocalDate.of(1978, 5, 23)).build());

        assertEquals(userController.getEmployeesById(1).getName(),
                "Alexey",
                "Валидация выполнется некорректно");
        assertEquals(userController.getEmployeesById(1).getEmail(),
                "asdfgqwe@gmail.com",
                "Валидация выполнется некорректно");
    }

    @Test
    public void userEmailValidation(){
        Set<ConstraintViolation<User>> violations = validator.validate(badEmailUser);
        ConstraintViolation<User> violation = violations.stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        assertEquals("Неверный адрес", violation.getMessageTemplate());
    }

    @Test
    public void userBirthdayValidation(){
        Set<ConstraintViolation<User>> violations = validator.validate(badBirthdateUser);
        ConstraintViolation<User> violation = violations.stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Отсутствует ошибка валидации"));

        assertEquals("Input valid birthdate", violation.getMessageTemplate());
    }
}