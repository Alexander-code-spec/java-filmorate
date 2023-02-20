package ru.yandex.practicum.filmorate.storage;

import org.aspectj.weaver.loadtime.definition.LightXMLParser;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage extends Storage<Film> {

    List<Film> getLikesCount();

    List<Film> getFilmsByIds(List<Integer> filmsIds);
    
    List<Film> getDirectorFilmsByYear(Integer id);

    List<Film> getDirectorFilmsByRating(Integer id);
}
