package ru.yandex.practicum.filmorate.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Predictor {

    private final Map<Integer, Map<Integer, Integer>> differentEvaluationMatrix = new HashMap<>();

    private final Map<Integer, Map<Integer, Integer>> evaluationAmountMatrix = new HashMap<>();

    private final Map<Integer, Map<Integer, Integer>> inputData;

    private final Map<Integer, Map<Integer, Integer>> outputData = new HashMap<>();

    public Predictor(Map <Integer, Map<Integer, Integer>> inputData) {
        this.inputData = inputData;
    }

    public List<Integer> recommendFilms(Integer userId) {
        buildDifferencesMatrix();
        Map<Integer, Map<Integer, Integer>> resultMap = predictLikes(userId);
        return findFilmsForUser(resultMap, userId);
    }

    public void buildDifferencesMatrix() {
        for (Map<Integer, Integer> allFilmsWithLikes : inputData.values()) {
            for (Map.Entry<Integer, Integer> filmWithLike : allFilmsWithLikes.entrySet()) {
                if (!differentEvaluationMatrix.containsKey(filmWithLike.getKey())) {
                    differentEvaluationMatrix.put(filmWithLike.getKey(), new HashMap<>());
                    evaluationAmountMatrix.put(filmWithLike.getKey(), new HashMap<>());
                }
                for (Map.Entry<Integer, Integer> filmWithLike2 : allFilmsWithLikes.entrySet()) {
                    int oldCount = 0;
                    if (evaluationAmountMatrix.get(filmWithLike.getKey()).containsKey(filmWithLike2.getKey())) {
                        oldCount = evaluationAmountMatrix.get(filmWithLike.getKey()).get(filmWithLike2.getKey());
                    }
                    int oldDiff = 0;
                    if (differentEvaluationMatrix.get(filmWithLike.getKey()).containsKey(filmWithLike2.getKey())) {
                        oldDiff = differentEvaluationMatrix.get(filmWithLike.getKey()).get(filmWithLike2.getKey());
                    }
                    int observedDiff = filmWithLike.getValue() - filmWithLike2.getValue();
                    evaluationAmountMatrix.get(filmWithLike.getKey()).put(filmWithLike2.getKey(), oldCount + 1);
                    differentEvaluationMatrix.get(filmWithLike.getKey()).put(filmWithLike2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer filmId : differentEvaluationMatrix.keySet()) {
            for (Integer filmId2 : differentEvaluationMatrix.get(filmId).keySet()) {
                int oldValue = differentEvaluationMatrix.get(filmId).get(filmId2);
                int count = evaluationAmountMatrix.get(filmId).get(filmId2);
                differentEvaluationMatrix.get(filmId).put(filmId2, oldValue / count);
            }
        }
    }

    public Map<Integer, Map<Integer, Integer>> predictLikes(Integer userId) {
        Map<Integer, Integer> uPred = new HashMap<>();
        Map<Integer, Integer> uFreq = new HashMap<>();
        for (Integer filmId : differentEvaluationMatrix.keySet()) {
            uFreq.put(filmId, 0);
            uPred.put(filmId, 0);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> userData : inputData.entrySet()) {
            for (Integer j : userData.getValue().keySet()) {
                for (Integer k : differentEvaluationMatrix.keySet()) {
                    try {
                        int predictedValue = differentEvaluationMatrix.get(k).get(j) + userData.getValue().get(j);
                        int finalValue = predictedValue * evaluationAmountMatrix.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + evaluationAmountMatrix.get(k).get(j));
                    } catch (NullPointerException ignored) {
                    }
                }
            }
            HashMap<Integer, Integer> clean = new HashMap<>();
            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j) / uFreq.get(j));
                }
            }
            for (Integer j : inputData.get(userId).keySet()) {
                if (userData.getValue().containsKey(j)) {
                    clean.put(j, userData.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1);
                }
            }
            outputData.put(userData.getKey(), clean);
        }
        return outputData;
    }

    public List<Integer> findFilmsForUser(Map<Integer, Map<Integer, Integer>> resultMap, Integer userId) {
        List<Integer> recommendedFilms = new ArrayList<>();
        Map<Integer, Integer> filmsByUser = resultMap.get(userId);
        for (Map.Entry<Integer, Integer> film : filmsByUser.entrySet()) {
            if (film.getValue() > 0 && !inputData.get(userId).containsKey(film.getKey())) {
                recommendedFilms.add(film.getKey());
            }
        }
        return recommendedFilms;
    }
}
