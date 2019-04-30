package com.krypton.snetwork.controllers;

import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
public class GroupController {

    @Autowired
    private GroupServiceImpl groupService;

    @Autowired
    private ImageServiceImpl imageService;

    /**
     * post request for getting group data
     * @param request 	json group data
     * @return group parameters,profile bytes,posts and members
     */
    @PostMapping("/group_data")
    public HashMap<String, Object> groupData(@RequestBody HashMap<String, Long> request) {
        return new HashMap<>(){{
            put("group",groupService.getGroup(request.get("id")));
        }};
    }
    /**
     * get group image in base64 byte format
     * @param request 	group id
     * @return group image in base64 format
     */
    @PostMapping("/group_image")
    public byte[] groupImage(@RequestBody HashMap<String, Long> request) {
        // load group image from database
        byte[] image = groupService.getGroup(request.get("id"))
                .getProfilePhoto().getBytes();
        return Base64.encodeBase64(image);
    }
    /**
     * get group background in base64 byte format
     * @param request 	group id
     * @return group background in base64 format
     */
    @PostMapping("/group_background")
    public byte[] groupBackground(@RequestBody HashMap<String, Long> request) {
        // load group background from database
        byte[] background = groupService.getGroup(request.get("id"))
                .getBackgroundPhoto().getBytes();
        return Base64.encodeBase64(background);
    }
    /**
     * post request for creating new group
     * @param image 	 group photo
     * @param background group background photo
     * @param name  	 group name
     * @param admin 	 admin email
     * @return message "group already exist" or "group created"
     */
    @PostMapping("/new_group")
    public HashMap<String, String> newGroup(
            @RequestParam("image") MultipartFile image,
            @RequestParam("background") MultipartFile background,
            @RequestParam("name") String name,
            @RequestParam("admin") String admin
    ) {
        // check if room with that name exist
        if (groupService.groupExist(name)) {
            return new HashMap<>(){{
                put("response","group already exist");
            }};
        }else {
            // insert group photo to database
            imageService.insertImage(name, image);
            // insert group background to database
            imageService.insertBackground(name, background);
            // insert group to database
            groupService.insertGroup(name, admin);
            return new HashMap<>(){{
                put("response","group created");
            }};
        }
    }
}
