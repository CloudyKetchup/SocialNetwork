package com.krypton.snetwork.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;

@Controller
public class AuthController{
	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/auth",method = RequestMethod.GET, produces = "text/html")
    public String preference() {
        if (User.getInstance().loggedIn) {
			return "account.html";
		}
		return "auth.html";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestBody HashMap<String, String> formData) {
	    System.out.println(formData);
		return formData.toString();
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@RequestBody HashMap<String, String> formData) {
	    System.out.println(formData);
		return formData.toString();
	}
}