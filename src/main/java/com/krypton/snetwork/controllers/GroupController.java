package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupServiceImpl groupService;
    /**
     * get {@link Group} by id
     * @param id        {@link Group} id
     * @return {@link Group}
     */
    @GetMapping("/id={id:.+}")
    public Group getGroup(@PathVariable("id") Long id) {
        return groupService.getGroup(id);
    }
    /**
     * get {@link Group} by name,useful for search purpose
     * @param name      {@link Group} name
     */
    @GetMapping("/name={name:.+}")
    public Group getGroup(@PathVariable("name") String name) {
        return groupService.getGroup(name);
    }
    /**
     * create new {@link Group}
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
}
