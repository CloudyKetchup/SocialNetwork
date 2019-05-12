package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupServiceImpl groupService;

    private final UserServiceImpl userService;

    public GroupController(GroupServiceImpl groupService, UserServiceImpl userService) {
        this.groupService = groupService;
        this.userService  = userService;
    }
    /**
     * get {@link Group} by id
     *
     * @param id        {@link Group} id
     * @return {@link Group}
     */
    @GetMapping("/id={id:.+}")
    public Group getGroup(@PathVariable("id") Long id) {
        return groupService.getGroup(id);
    }
    /**
     * get {@link Group} by name,useful for search purpose
     *
     * @param name      {@link Group} name
     */
    @GetMapping("/name={name:.+}")
    public Group getGroup(@PathVariable("name") String name) {
        return groupService.getGroup(name);
    }
    @GetMapping("/followers/{id:.+}")
    public Set<User> getFollowers(@PathVariable("id") Long id) {
        return groupService.getGroup(id).getFollowers();
    }
    /**
     * create new {@link Group},add it to database and 
     *
     * @param profilePhoto 	 {@link Group} photo
     * @param background     {@link Group} background photo
     * @param name  	     {@link Group} name
     * @param admin     	 admin email
     * @return message "group already exist" or "group created"
     */
    @PostMapping("/new")
    public String newGroup(
            @RequestParam("image")      MultipartFile profilePhoto,
            @RequestParam("background") MultipartFile background,
            @RequestParam("name")       String name,
            @RequestParam("admin")      String admin
    ) {
        // check if room with that name exist
        if (groupService.groupExist(name)) {
            return "group already exist";
        }else {
            Group group = groupService.createGroup(name, admin);

            groupService.saveProfileAndBackgroundPicture(
                    group,
                    profilePhoto,
                    background
            );
            return "group created";
        }
    }
    /**
     * @param id        {@link Group} to follow
     * @param email     {@link User} who follows(follower)
     */
    @PostMapping("/follow/{id:.+}")
    public void followGroup(
        @PathVariable("id") Long id,
        @RequestParam       String email
    ) {
        Group group   = groupService.getGroup(id);
        User follower = userService.getUser(email);

        groupService.followGroup(group, follower);
    }
    /**
     * @param id        {@link Group} to unFollow
     * @param email     {@link User} follower
     */
    @PostMapping("/unFollow/{id:.+}")
    public void unFollowGroup(
            @PathVariable("id") Long id,
            @RequestParam       String email
    ) {
        Group group   = groupService.getGroup(id);
        User follower = userService.getUser(email);

        groupService.unFollowGroup(group, follower);
    }
}
