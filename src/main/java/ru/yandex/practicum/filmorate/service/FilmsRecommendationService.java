package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilmsRecommendationService {

    public List<Integer> recommendFilmsByUserId(Map <Integer, Map<Integer, Integer>> existingData, Integer userId) {
        Predictor predictor = new Predictor(existingData);
        return predictor.recommendFilms(userId);
    }
}
