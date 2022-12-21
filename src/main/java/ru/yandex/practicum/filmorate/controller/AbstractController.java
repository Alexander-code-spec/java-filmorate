package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Optional;

@Data
@Slf4j
public abstract class AbstractController<T> {
    public T abstractCreate(T obj, HashMap<Integer, T> map, Integer id) {
        map.put(id, obj);
        log.debug("Создан новый объект: {}", obj);
        return obj;
    }

    public T abstractUpdate(T obj, HashMap<Integer, T> map, Integer id){
        map.put(id, obj);
        log.debug("Изменен объект: {}", obj);
        return obj;
    }

    public void valid(T obj,
                      Integer id,
                      HashMap<Integer, T> map,
                      boolean create){
        Optional<T> optionalObj = Optional.ofNullable(obj);

        if(optionalObj.isPresent()) {
            if (!create &&  !map.containsKey(id)){
                throw new ValidationException("Невозможно онбовить данные объекта с id = " + id +
                        ", такого объекта не сущесвтует");
            } else if (create && map.containsKey(id)){
                throw new ValidationException("Объект с id = {} уже сущесвтует");
            }
        }  else {
            throw new ValidationException("Object not present!");
        }
    }
}
