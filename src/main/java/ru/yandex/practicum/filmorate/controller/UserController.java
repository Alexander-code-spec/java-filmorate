package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        if(userService.getUserStorage().get(id) == null){
            throw new ObjectNotFoundException("Пользователь не существует!");
        }
        return userService.getUserStorage().get(id);
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
        if(userService.getUserStorage().delete(user) == null){
            throw new ObjectNotFoundException("Пользователь не существует!");
        }
        return true;
    }
}
