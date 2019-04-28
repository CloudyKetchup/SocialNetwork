package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * post request for getting user groups list
     * @param request 	user account data
     * @return user groups entities
     */
    @PostMapping("/user_groups")
    public HashMap<String, Object> userGroups(@RequestBody HashMap<String, String> request) {
        return new HashMap<>(){{
            put("groups",userService.getUser(request.get("email")).getGroups());
        }};
    }
    /**
     * get member profile image
     * @param request 	member email
     * @return member profile image in base64 format
     */
    @PostMapping("/user_image")
    public byte[] memberImage(@RequestBody HashMap<String,String> request) {
        User member   = userService.getUser(request.get("email"));
        byte[] image  = member.getProfilePhoto().getBytes();

        return Base64.encodeBase64(image);
    }
}
