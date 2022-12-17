package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class User {
    private int id;
    @Email(message = "Неверный адрес")
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name = "common";
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Input valid birthdate")
    private LocalDate birthday;
}
