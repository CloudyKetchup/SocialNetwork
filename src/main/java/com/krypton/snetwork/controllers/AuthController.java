package com.krypton.snetwork.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
public class AuthController{
	
	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/auth",method = RequestMethod.GET, produces = "text/html")
    public String auth() {
		return "auth.html";
    }

    @RequestMapping(value = "/account",method = RequestMethod.GET, produces = "text/html")
    public String account() {
    	return "account.html";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public HashMap<String, Object> login(@RequestBody HashMap<String, String> formData) {
		// data from post
		String email = formData.get("email");
		String password = formData.get("password");
		// body for response
		HashMap<String, Object> response = new HashMap<>();
		// get user from database by email
		User dbUser = loadUserFromDatabase(email);
		// check if user with that email exist in database
		if (dbUser != null) {
			// check password match
			if (dbUser.getPassword().equals(password)) {
				// json body
				response.put("response", "login success");
				response.put("userBody", dbUser);
			}else {
				response.put("response","wrong password");
			}	
		}else {
			response.put("response","email not exist");
		}
		return response;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> register(@RequestBody HashMap<String, String> formData) {
	    HashMap<String, String> response = new HashMap<>();
		// check if email does already exist
	    if (userExist(formData.get("email"))) {
	    	response.put("response","account exist");
	    }else {
	    	saveUserToDatabase(formData);
	    	response.put("response","registered");
		}
		return response;
	}
	// check if email already exist in database
	private boolean userExist(String email) {
		Optional<String> findedEmail = userRepository.findEmail(email);
		if (findedEmail.isPresent()) {
			return true;
		}
		return false;
	}

	// load user from database
	private User loadUserFromDatabase(String email) {
		return userRepository.findByEmail(email).get();	
	}
	// save new user account to database
	private void saveUserToDatabase(HashMap<String, String> formData) {
		// save user to database
		userRepository.save(
			new User(
				formData.get("username"),
				formData.get("email"),
				formData.get("password")
			)
		);
	}
}