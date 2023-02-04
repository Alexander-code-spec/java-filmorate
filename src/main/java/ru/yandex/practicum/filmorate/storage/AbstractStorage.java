package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Data
@Slf4j
public abstract class AbstractStorage<T> {
    private HashMap<Integer, T> map = new HashMap<>();

    public T create(T obj, Integer id) throws ValidationException {
        valid(obj, id, true);
        map.put(id, obj);
        log.debug("Создан объект: {}", obj);
        return obj;
    }

    public T update(T obj, Integer id) throws ValidationException {
        valid(obj, id, false);
        map.put(id, obj);
        log.debug("Изменен объект: {}", obj);
        return obj;
    }

    public List<T> find() {
        return new ArrayList<>(map.values());
    }

    public void valid(T obj,
                      Integer id,
                      boolean create) throws ValidationException {
        Optional<T> optionalObj = Optional.ofNullable(obj);

        if(optionalObj.isPresent() && id != null) {
            if (!create &&  !map.containsKey(id)){
                throw new ObjectNotFoundException("Невозможно обновить данные объекта с id = " + id +
                        ", такого объекта не сущесвтует");
            } else if (create && map.containsKey(id)){
                throw new ValidationException("Объект с id = " + id + " уже сущесвтует");
            }
        }  else {
            throw new ObjectNotFoundException("Object not present!");
        }
    }
}
