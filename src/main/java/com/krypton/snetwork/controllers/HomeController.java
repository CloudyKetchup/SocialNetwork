package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.io.*;

/**
 * Controller responding for homepage
 */
@RestController
public class HomeController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageRepository imageRepository;

	private String homeFolder = System.getProperty("user.home");
	/**
	 * post request for getting user data
	 * @param account json user account data  
	 */
	@RequestMapping("/user_data")
	public HashMap<String, Object> userData(@RequestBody HashMap<String, String> account) {
	    // return user entity in json
	    return new HashMap<>(){{
			put("groups",getUser(account.get("email")).getGroups());
		}};
	}
	/**
	 * post request for getting group data
	 * @param group json group data
	 */
	@RequestMapping("/group_data")
	public HashMap<String, Object> groupData(@RequestBody HashMap<String, String> group) {
		// return group entity in json
		return new HashMap<>(){{
			put("group",getGroup(Long.valueOf(group.get("id"))));
		}};
	}
	/**
	 * post request for creating new group
	 * @param image group image from form data
	 * @param name  group name
	 * @param admin admin email
	 */
	@RequestMapping("/new_group")
	public HashMap<String, String> newGroup(
		@RequestParam("image") MultipartFile image,
		@RequestParam("name") String name,
		@RequestParam("admin") String admin
	) {
		// response body
		HashMap<String, String> response = new HashMap<>(1);
		// check if room with that name exist
		if (groupExist(name)) {
			response.put("response","group already exist");
		}else {
			// insert image to database
			insertImage(name, image);
			// insert group to database
			insertGroup(name, admin);
			response.put("response","group created");
		}
		return response;
	}
	/**
	 * post request for new group post
	 * @param post group post data like group id,content...
	 */
	@RequestMapping("/new_post")
	public void newPost(@RequestBody HashMap<String, String> post) {
		// group where to save new post
		Group group = getGroup(Long.valueOf(post.get("group_id")));
		// get author entity from database
		User author = getUser(post.get("author"));
		// add new post to group
		group.getPosts().add(
			// post entity object
			new Post(post.get("content"),author,Long.valueOf(post.get("time")))
		);
		// update group in database
		groupRepository.save(group);
	}
	/**
	 * post request for adding like to post in group
	 * @param like group,post and id
	 */
	@RequestMapping("/add_like")
	public void addLike(@RequestBody HashMap<String, String> like) {
		// get post likes
		getPost(like).getLikes()
			// add user id to likes
			.add(Long.valueOf(like.get("user_id")));
		// update group containing posts with likes
		groupRepository.save(
			getGroup(Long.valueOf(like.get("group_id")))
		);
	}
	/**
	 * post request for removing like from post in group
	 * @param like group,post and id
	 */
	@RequestMapping("/remove_like")
	public void removeLike(@RequestBody HashMap<String, String> like) {
		// get post likes
		getPost(like).getLikes()
			// remove user id from post likes
			.remove(Long.valueOf(like.get("user_id")));
		// update group containing posts with likes
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

	@RequestMapping("/find_member")
	public User findUser(@RequestBody HashMap<String, String> user) {
		return userRepository.findByName(user.get("username"));
	}
	/**
	 * check if group with selected name exist
	 * @param name group name
	 */
	private boolean groupExist(String name) {
		return getGroup(name) != null;
	}
	/**
	 * insert new group to database
	 * @param name  group name
	 * @param email admin email
	 */
	private void insertGroup(String name, String email) {
		// get admin entity from database
		User admin  = getUser(email);
		// get group image entity from database
		Image image = getImage(name + "-image");
		// create group entity and return as object
		Group group = createGroup(name, name, image);
		// save new group entity to admin
		saveGroupMember(group, admin);
		// save admin entity to group
		saveMemberGroup(group, admin);
	}
	/**
	 * create image entity and
	 * insert new image to database
	 * @param name  group name
	 * @param image image file
	 */
	private void insertImage(String name, MultipartFile image) {
		try {
			// group image entity
			Image imageEntity = createImage(name, image);
			// save image to datbase
			imageRepository.save(imageEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * create group entity object
	 * @param name  group name
	 * @param admin admin email
	 * @param image group image entity
	 */
	private Group createGroup(String name, String admin,Image image) {
		// group entity
		return new Group(name,getUser(admin),image);
	}
	/** 
	 * create image entity object
	 * @param name  group name
	 * @param image image file
	 */
	private Image createImage(String name,MultipartFile image) throws IOException {
		return new Image(
			name + "-image",		// image file name
			image.getContentType(),	// file type
			image.getBytes()		// file bytes
		);
	}
	/**
	 * add member entity to group members list
	 * @param group  entity
	 * @param member entity
	 */
	private void saveGroupMember(Group group, User member) {
		group.getMembers().add(member);
		groupRepository.save(group);
	}
	/**
	 * add group entity to member groups list
	 * @param group  entity
	 * @param member entity
	 */
	private void saveMemberGroup(Group group, User member) {
		member.getGroups().add(group);
		userRepository.save(member);
	}
	/**
	 * get user entity object from database by email
	 * @param email
	 */
	private User getUser(String email) {
		return userRepository.findByEmail(email);
	}
	/**
	 * get group entity object from database by name
	 * @param name
	 */
	private Group getGroup(String name) {
		return groupRepository.findByName(name);
	}
	/**
	 * get group entity object from database by id
	 * @param id
	 */
	private Group getGroup(Long id) {
		return groupRepository.findById(id).get();
	}
	/**
	 * get image entity object from 
	 * @param name image name
	 */
	private Image getImage(String name) {
		return imageRepository.findByName(name);
	}
	/**
	 * find group post from database by id
	 * first find group,then get posts from group
	 * @param requestBody json body from request
	 */
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
