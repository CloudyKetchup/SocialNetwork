package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.group.*;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * get {@link User} feed {@link Post}'s,from groups and users who this user follows
     *
     * @param id            user id
     * @return list of posts entities
     */
    @GetMapping("/feed/{id:.+}")
    public Set<Post> feedPosts(@PathVariable("id") Long id) {
        // account requesting feed posts
        User user = userService.getUser(id);

        return userService.getFeedPosts(user);
    }
    /**
     * get {@link User} {@link Post}'s
     *
     * @param email     {@link User} post
     * @return {@link Post}'s
     */
    @GetMapping("/posts/{email:.+}")
    public Set<Post> getPosts(@PathVariable("email") String email) {
        return userService.getUser(email).getPosts();
    }
    /**
     * get {@link User} followers
     *
     * @param id        {@link User} id
     * @return {@link User} followers
     */

    @GetMapping("/followers/{id:.+}")
    public Set<User> getFollowers(@PathVariable("id") Long id) {
        return userService.getUser(id).getFollowers();
    }
    /**
     * get {@link User}'s who {@link User} follows
     *
     * @param id        {@link User} id
     * @return set containing {@link User}'s
     */
    @GetMapping("/following/{id:.+}")
    public Set<User> getFollowing(@PathVariable("id") Long id) {
        return userService.getUser(id).getFollowing();
    }
    /**
     * get {@link User} {@link Group}'s
     *
     * @param email     {@link User} email
     * @return {@link Group}'s
     */
    @GetMapping("/groups/{email:.+}")
    public Set<Group> getGroups(@PathVariable("email") String email) {
        return userService.getUser(email).getGroups();
    }
    /**
     * get {@link User} by name
     *
     * @param name      {@link User} name
     * @return {@link User} entity
     */
    @GetMapping("/name={name:.+}")
    public User getUser(@PathVariable("name") String name) {
        return userService.getUserByName(name);
    }
    /**
     * @param id        {@link User} who to follow
     * @param email     {@link User} who follows(follower)
     */
    @PostMapping("/follow/{id:.+}")
    public void followUser(
        @PathVariable("id") Long id,
        @RequestParam       String email
    ) {
        User user     = userService.getUser(id);
        User follower = userService.getUser(email);

        userService.followUser(user, follower);
    }
    /**
     * @param id        {@link User} who to unFollow
     * @param email     {@link User} follower
     */
    @PostMapping("/unFollow/{id:.+}")
    public void unFollowUser(
            @PathVariable("id") Long id,
            @RequestParam       String email
    ) {
        User user     = userService.getUser(id);
        User follower = userService.getUser(email);

        userService.unFollowUser(user, follower);
    }
}
