package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.Group;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class HomeController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/user_data")
	public HashMap<String, Object> userData(@RequestBody HashMap<String, String> post) {
		return userData(getUser(post.get("email")));
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
	private void insertGroup(String groupName,String admin) {
		groupRepository.save(createGroup(groupName,admin));
	}
	// create group object
	private Group createGroup(String groupName,String admin) {
		// create group with name and admin(by default admin is who created room)
		Group group = new Group(groupName,getUser(admin));
		// add admin to group members
		memberToGroup(group,admin);
		return group;
	}
	// add member to a group
	private void memberToGroup(Group group, String username) {
		group.getMembers().add(getUser(username));
	}
	// add group to a member
	private void groupToMember(User member, String groupname) {
		member.getGroups().add(getGroup(groupname));
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
