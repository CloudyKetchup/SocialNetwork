package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.Post;
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
     * get {@link User} feed {@link Post}'s,from groups and users who this user follows
     * @param id            user id
     * @return list of posts entities
     */
    @GetMapping("/feed/{id:.+}")
    public LinkedList<Post> feedPosts(@PathVariable("id") Long id) {
        // account requesting feed posts
        User user = userService.getUser(id);

        LinkedList<Post> feedPosts = new LinkedList<>(user.getPosts());
        // all posts from groups user follows
        for (Group group : user.getGroups()) {
            feedPosts.addAll(group.getPosts());
        }
        return feedPosts;
    }
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
