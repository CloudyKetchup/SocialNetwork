package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class AuthController{

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }
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
     * login procedure
     * @param  formData 	user email and password
     * @return response message,may contain {@link User} entity
     */
    @PostMapping("/login")
    @ResponseBody
    public HashMap<String, Object> login(@RequestBody HashMap<String, String> formData) {
        String email 	= formData.get("email");    // request email
        String password = formData.get("password"); // request password

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
     * @param form      data for registration
     * @return message "account exist" or "registered"
     */
    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody HashMap<String, String> form) {
        String name     = form.get("name");
        String surname  = form.get("surname");
        String email 	= form.get("email");
        String password = form.get("password");

        // check if email does already exist
        if (userService.userExist(email)) {
            return "account exist";
        }else {
            // save user to database
            userService.saveUser(name, surname, email, password);
            return "registered";
        }
    }
}