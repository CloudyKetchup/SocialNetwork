package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

/**
 * Controller responding for homepage
 */
@RestController
public class HomeController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

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
	 * @param group group data from request
	 */
	@RequestMapping("/new_group")
	public HashMap<String, String> newGroup(@RequestBody HashMap<String, String> group) {
		// response body
		HashMap<String, String> response = new HashMap<>(1);
		// group name
		String name 	  = (String) group.get("name");
		// group admin email
		String admin 	  = (String) group.get("admin");
		// path to group image
		String imagePath = (String) group.get("imagepath");
		// check if room with that name exist
		if (groupExist(name)) {
			response.put("response","group already exist");
		}else {
			// insert group to database
			insertGroup(name,admin,imagePath);
			response.put("response","group created");
		}
		return response;
	}
	/**
	 * post request for saving group image to local disk
	 * and sending back file path for future group entity
	 * @param image group image file from request 
	 */
	@RequestMapping(
		value 	 = "/group_image",
		consumes = "multipart/form-data",
		method 	 = RequestMethod.POST
	)
	public HashMap<String, String> groupImage(@RequestParam("image") MultipartFile image) {
		// save file locally and assign file path
		String filePath = saveFile(image);
		System.out.println(filePath);
		return new HashMap<>(){{
			// check if file was saved
			if (filePath != null) {
				put("response","image saved");
				// send back file path
				put("imagepath",filePath);
			}else {
				put("response","error occurred");
			}
		}};
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
		group.getPosts().add(new Post(post.get("content"),author,Long.valueOf(post.get("time"))));
		// update group in database
		groupRepository.save(group);
	}
	/**
	 * add like to post in group
	 * @param like
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
	 * remove like from post in group
	 * @param like
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

	@RequestMapping("/find_user")
	public User findUser(@RequestBody HashMap<String, String> user) {
		return userRepository.findByName(user.get("username"));
	}
	// check if group with name exist
	private boolean groupExist(String groupName) {
		return getGroup(groupName) != null;
	}
	// insert new group to database
	private void insertGroup(String groupName, String admin, String groupImage) {
		User user   = getUser(admin);
		Group group = createGroup(groupName, admin, groupImage);
		addGroupMember(group, user);
		addMemberGroup(group, user);
	}
	// create group object
	private Group createGroup(String groupName, String admin,String groupImage) {
		// create group with name and admin(by default admin is who created room)
		return new Group(groupName,getUser(admin),groupImage);
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
	private String saveFile(MultipartFile file) {
		try {
			// full path to file
		    String filepath = Paths.get(
		    	homeFolder,file.getOriginalFilename()
		    ).toString();
		    // save the file locally
		    new FileOutputStream(new File(filepath))
		    	.write(file.getBytes());
		    return filepath;
	    }catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
}
