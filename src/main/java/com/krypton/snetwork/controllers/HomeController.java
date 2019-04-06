package com.krypton.snetwork.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
public class HomeController {
	
	@RequestMapping(value = "/userdata",method = RequestMethod.POST)
	public HashMap<String, String> userData(@RequestBody HashMap<String, String> user) {
		return user;
	}
}
