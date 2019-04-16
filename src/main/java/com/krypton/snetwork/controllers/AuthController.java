package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController{

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(
		value = "/auth",
		method = RequestMethod.GET,
		produces = "text/html"
	)
	public String auth() {
		return "auth.html";
    }

    @RequestMapping(
    	value = "/account",
    	method = RequestMethod.GET,
    	produces = "text/html"
    )
    public String account() {
    	return "account.html";
    }

    @RequestMapping("/login")
	@ResponseBody
	public HashMap<String, Object> login(@RequestBody HashMap<String, String> formData) {
		String email = formData.get("email");
		String password = formData.get("password");
		// body for response
		HashMap<String, Object> response;
		// get user from database
        User dbUser = loadUser(email);
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

	@RequestMapping("/register")
	@ResponseBody
	public Map<String, String> register(@RequestBody HashMap<String, String> formData) {
	    HashMap<String, String> response = new HashMap<>();
		// check if email does already exist
	    if (userExist(formData.get("email"))) {
	    	response.put("response","account exist");
	    }else {
	    	saveUser(formData);
	    	response.put("response","registered");
		}
		return response;
	}
	// check if email already exist in database
	private boolean userExist(String email) {
		return userRepository.findEmail(email).isPresent();
	}

	// load user from database
	private User loadUser(String email) {
		return userRepository.findByEmail(email);
	}
	// save new user account to database
	private void saveUser(HashMap<String, String> formData) {
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