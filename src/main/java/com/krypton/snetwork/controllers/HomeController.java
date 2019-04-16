package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
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
	public HashMap<String, Object> userData(@RequestBody HashMap<String, String> account) {
	    return new HashMap<>(){{
			put("groups",getUser(account.get("email")).getGroups());
		}};
	}

	@RequestMapping("/group_data")
	public HashMap<String, Object> groupData(@RequestBody  HashMap<String, String> group) {
		return new HashMap<>(){{
			put("group",getGroup(Long.valueOf(group.get("id"))));
		}};
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

	@RequestMapping("/new_post")
	public HashMap<String, String> newPost(@RequestBody HashMap<String, String> post) {
		// group where to save new post
		Group group = getGroup(Long.valueOf(post.get("group_id")));
		// get author entity from database
		User author = getUser(post.get("author"));
		// add new post to group
		group.getPosts().add(new Post(post.get("content"),author,Long.valueOf(post.get("time"))));
		// update group in database
		groupRepository.save(group);
		return post;
	}

	@RequestMapping("/add_like")
	public void addLike(@RequestBody HashMap<String, String> like) {
		getPost(like).getLikes()
			.add(Long.valueOf(like.get("user_id")));
		groupRepository.save(
			getGroup(Long.valueOf(like.get("group_id")))
		);
	}

	@RequestMapping("/remove_like")
	public void removeLike(@RequestBody HashMap<String, String> like) {
		getPost(like).getLikes()
			.remove(Long.valueOf(like.get("user_id")));
		groupRepository.save(
			getGroup(Long.valueOf(like.get("group_id")))
		);
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
		return getGroup(groupName) != null;
	}
	// insert new group to database
	private void insertGroup(String groupName, String admin) {
		User user   = getUser(admin);
		Group group = createGroup(groupName, admin);
		addGroupMember(group, user);
		addMemberGroup(group, user);
	}
	// create group object
	private Group createGroup(String groupName, String admin) {
		// create group with name and admin(by default admin is who created room)
		return new Group(groupName,getUser(admin));
	}
	// add member to group
	private void addGroupMember(Group group, User member) {
		group.getMembers().add(member);
		groupRepository.save(group);
	}
	// add group to member
	private void addMemberGroup(Group group, User member) {
		member.getGroups().add(group);
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
	private Group getGroup(Long id) {
		return groupRepository.findById(id).get();
	}
	// find post by id
	private Post getPost(HashMap<String, String> requestBody) {
		final Post[] posts = new Post[1];
		// get group where that contains post
		getGroup(Long.valueOf(requestBody.get("group_id")))
			// find post by id
			.getPosts().forEach(post -> {
				if (post.getId().equals(Long.valueOf(requestBody.get("post_id")))){
					posts[0] = post;
				}
			});
		return posts[0];
	}
}
