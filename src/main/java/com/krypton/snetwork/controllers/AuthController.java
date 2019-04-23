package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.service.user.UserServiceImpl;
import com.krypton.snetwork.service.image.ImageServiceImpl;
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
        HashMap<String, String> parsedData = stringToHashMap(data);
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
            // insert user profile bytes to database
            imageService.insertImage(email,image);
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
    /**
     * parses json from string to hashmap
     * @param json		String with keys and values
     */
    public static HashMap<String, String> stringToHashMap(String json) {
        // hashmap parsed from string
        HashMap<String, String> parsedHashMap = new HashMap<>();
        // remove curly brackets
        json = json.substring(1, json.length()-1);
        // split string to create key/value pairs
        String[] keyValuePairs = json.split(",");
        // iterate pairs
        for (String pair : keyValuePairs) {
            // split pair in key and value
            String[] entry = pair.split(":");
            // put key and value to parsed hashmap
            parsedHashMap.put(
                    entry[0].replace('"',' ').trim(),
                    entry[1].replace('"',' ').trim()
            );
        }
        return parsedHashMap;
    }
}