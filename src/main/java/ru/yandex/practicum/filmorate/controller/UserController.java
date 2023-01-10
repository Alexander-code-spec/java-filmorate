package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.yaml.snakeyaml.util.EnumUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.getUserStorage().findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getEmployeesById(@PathVariable("id") Integer id) {
        if(!userService.getUserStorage().getMap().containsKey(id)){
            throw new ObjectNotFoundException("Пользователь не существует!");
        }
        return userService.getUserStorage().getMap().get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId){
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId){
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Integer id){
        if(id == null) {
            return null;
        }
        List<User> userList = userService.getFriendList(id);
        return userService.getFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getMutalFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        if(id == null || friendId == null) {
            return null;
        }

        ArrayList<User> list = new ArrayList<>(userService.getFriendList(id));
        list.retainAll(userService.getFriendList(friendId));
        return list;
    }

    @PostMapping
    @Validated
    public User create(@RequestBody @Valid  User user) throws ValidationException {
        return userService.getUserStorage().create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws ValidationException {
        return userService.getUserStorage().update(user);
    }

    @DeleteMapping
    public Boolean deleteUser(@Valid @RequestBody User user){
        if(!userService.getUserStorage().getMap().containsKey(user.getId())){
            throw new ObjectNotFoundException("Пользователь не существует!");
        }
        return userService.getUserStorage().delete(user);
    }

    public HashMap<Integer, User> getUsers() {
        return userService.getUserStorage().getMap();
    }
}
