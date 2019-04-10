package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.Group;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;

@RestController
public class HomeController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/user_data")
	public HashMap<String, Object> userData(@RequestBody HashMap<String, String> post) {
        HashMap<String, Object> response = new HashMap<>();
	    response.put("groups",getUser(post.get("email")).getGroups());
        return response;
	}

	@RequestMapping("/new_group")
	public HashMap<String, String> newGroup(@RequestBody HashMap<String, String> group) {
		// response body
		HashMap<String, String> response = new HashMap<>(1);
		// group name
		String name = group.get("name");
		// check if room with that name exist
		if (groupExist(name)) {
			response.put("response","group already exist");
		}else {
			insertGroup(name,group.get("admin"));
			response.put("response","group created");
		}
		return response;
	}

	@RequestMapping("/add_member")
	public HashMap<String, String> addMember(@RequestBody HashMap<String, String> member) {
		// response body
		HashMap<String, String> response = new HashMap<>();
		return member;
	}

	@RequestMapping("/find_user")
	public User findUser(@RequestBody HashMap<String, String> user) {
		return userRepository.findByName(user.get("username"));
	}
	// check if group with name exist
	private boolean groupExist(String groupName) {
		return groupRepository.findByName(groupName) != null;
	}
	// insert new group to database
	private void insertGroup(String groupName, String admin) {
		User user = getUser(admin);
		Group group = createGroup(groupName, admin);
		addGroup(group, user);
		addMember(group, user);
	}
	// create group object
	private Group createGroup(String groupName, String admin) {
		// create group with name and admin(by default admin is who created room)
		return new Group(groupName,getUser(admin));
	}
	// add group to user groups list
	private void addGroup(Group group, User member) {
		group.addMember(member);
		groupRepository.save(group);
	}
	// add member to group members list
	private void addMember(Group group, User member) {
		member.addGroup(group);
		userRepository.save(member);
	}
	// get user entity object from database
	private User getUser(String email) {
		return userRepository.findByEmail(email);
	}
	// get group entity object from database
	private Group getGroup(String name) {
		return groupRepository.findByName(name);
	}
	// get user data from database
	private HashMap<String, Object> userData(User user) {
		HashMap<String, Object> response = new HashMap<>(1);
		response.put("groups",user.getGroups());
		return response;
	}
}
