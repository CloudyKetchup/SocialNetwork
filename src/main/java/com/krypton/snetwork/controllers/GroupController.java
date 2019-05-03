package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class GroupController {

    @Autowired
    private GroupServiceImpl groupService;

    @Autowired
    private ImageServiceImpl imageService;
    /**
     * @param id        group id
     * @return {@link Group} from database
     */
    @GetMapping("/get_group/{id:.+}")
    public Group getGroup(@PathVariable("id") Long id) {
        return groupService.getGroup(id);
    }
    /**
     * get group image,will come on client side like resource
     * @param id 	    group id
     * @return group {@link Image}
     */
    @GetMapping("/group/image/{id:.+}")
    public ResponseEntity<byte[]> getGroupImage(@PathVariable("id") Long id) {
        // load group image from database
        byte[] image = imageService.getImage(id).getBytes();
        // return as resource
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    /**
     * get group background
     * @param id 	    group id
     * @return group background {@link Image}
     */
    @GetMapping("/group/background/{id:.+}")
    public ResponseEntity<byte[]> getGroupBackground(@PathVariable("id") Long id) {
        // load group background from database
        byte[] image = imageService.getImage(id).getBytes();
        // return as resource
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
    /**
     * creating new {@link Group} and save to database
     * @param image 	 group photo
     * @param background group background photo
     * @param name  	 group name
     * @param admin 	 admin email
     * @return message "group already exist" or "group created"
     */
    @PostMapping("/new_group")
    public String newGroup(
            @RequestParam("image")      MultipartFile image,
            @RequestParam("background") MultipartFile background,
            @RequestParam("name")       String name,
            @RequestParam("admin")      String admin
    ) {
        // check if room with that name exist
        if (groupService.groupExist(name)) {
            return "group already exist";
        }else {
            // insert group photo to database
            imageService.insertProfilePicture(name, image);
            // insert group background to database
            imageService.insertBackground(name, background);
            // insert group to database
            groupService.insertGroup(name, admin);
            return "group created";
        }
    }
}
