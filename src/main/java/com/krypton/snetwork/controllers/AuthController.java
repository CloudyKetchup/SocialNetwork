package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.common.Tools;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Controller
public class AuthController{

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private Tools tools;

    /**
     * redirect to registration page
     * @return 		registration page
     */
    @GetMapping(
            value 	 = "/register",
            produces = "text/html"
    )
    public String registerPage() {
        return "register.html";
    }
    /**
     * redirect to login page
     * @return 		login page
     */
    @GetMapping(
            value 	 = "/login",
            produces = "text/html")
    public String loginPage() {
        return "login.html";
    }
    /**
     * user login procedure
     * @param  formData 	user email and password
     * @return response message or response message with user data
     */
    @PostMapping("/login")
    @ResponseBody
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> formData) {
        String email 	= formData.get("email");
        String password = formData.get("password");
        // body for response
        HashMap<String, Object> response;
        // get user from database
        User dbUser = userService.getUser(email);
        if (dbUser != null) {
            // check if password is correct
            if (dbUser.getPassword().equals(password)) {
                response = new HashMap<>(){{
                    put("response", "login success");
                    put("account", dbUser);
                }};
            }else {
                response = new HashMap<>(){{
                    put("response","wrong password");
                }};
            }
        }else {
            response = new HashMap<>(){{
                put("response","email not exist");
            }};
        }
        return response;
    }
    /**
     * user registration procedure
     * @param image		user profile bytes
     * @param data 		user name,email,password
     * @return message "account exist" or "registered"
     */
    @PostMapping("/register")
    @ResponseBody
    public HashMap<String, String> register(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data")  String data
    ) {
        // parsed json from request
        HashMap<String, String> parsedData = tools.stringToHashMap(data);
        // username from request
        String username = parsedData.get("username");
        // email from request
        String email 	= parsedData.get("email");
        // password from request
        String password = parsedData.get("password");
        // check if email does already exist
        if (userService.userExist(email)) {
            return new HashMap<>(){{
                put("response","account exist");
            }};
        }else {
            // insert user profile photo to database
            imageService.insertProfilePicture(email,image);
            // save user entity to database
            userService.saveUser(
                    username,
                    email,
                    password,
                    imageService.getImage(email)
            );
            return new HashMap<>(){{
                put("response","registered");
            }};
        }
    }
}