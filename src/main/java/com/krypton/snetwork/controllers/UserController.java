package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.*;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;
    /**
     * get {@link User} {@link Group}'s list
     * @param email     {@link User} email
     * @return {@link Group}'s list
     */
    @GetMapping("/groups/{email:.+}")
    public LinkedList<Group> userGroups(@PathVariable("email") String email) {
        return new LinkedList<>(userService.getUser(email).getGroups());
    }
    /**
     * get {@link User} by name 
     * @param name      {@link User} name
     * @return {@link User} entity
     */
    @GetMapping("/name={name:.+}")
    public User getUser(@PathVariable("name") String name) {
        return userService.getUserByName(name);
    }
}
