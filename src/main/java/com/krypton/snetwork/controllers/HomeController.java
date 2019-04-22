package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;


@RestController
public class HomeController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupServiceImpl groupService;

	@Autowired
    private ImageServiceImpl imageService;

	@Autowired
	private UserServiceImpl userService;

	/**
	 * post request for getting user groups list
	 * @param account 	json user account data
	 * @return user groups entities
	 */
	@RequestMapping("/user_groups")
	public HashMap<String, Object> userGroups(@RequestBody HashMap<String, String> account) {
	    // user entity
	    User user = userService.getUser(account.get("email"));
	    // return user entity in json
	    return new HashMap<>(){{
			put("groups",user.getGroups());
		}};
	}
	/**
	 * post request for getting group data
	 * @param group 	json group data
	 * @return group parameters,profile bytes,posts and members
	 */
	@RequestMapping("/group_data")
	public HashMap<String, Object> groupData(@RequestBody HashMap<String, String> group) {
		// return group entity in json
		return new HashMap<>(){{
			put("group",groupService.getGroup(Long.valueOf(group.get("id"))));
		}};
	}
	/**
	 * get group image in base64 byte format
	 * @param group 	group id
	 * @return group image in base64 format
	 */
	@RequestMapping("/group_image")
    public byte[] groupImage(@RequestBody HashMap<String, String> group) {
		// group image bytes
        byte[] image = groupService.getGroup(Long.valueOf(group.get("id")))
				.getGroupImage().getBytes();
		return Base64.encodeBase64(image);
    }
	/**
	 * post request for creating new group
	 * @param image 	group photo from form data
	 * @param name  	group name
	 * @param admin 	admin email
	 * @return message "group already exist" or "group created"
	 */
	@RequestMapping("/new_group")
	public HashMap<String, String> newGroup(
		@RequestParam("image") MultipartFile image,
		@RequestParam("name") String name,
		@RequestParam("admin") String admin
	) {	
		// check if room with that name exist
		if (groupService.groupExist(name)) {
			return new HashMap<>(){{
				put("response","group already exist");
			}};
		}else {
			// insert photo to database
			imageService.insertImage(name, image);
			// insert group to database
			groupService.insertGroup(name, admin);
			return new HashMap<>(){{
				put("response","group created");
			}};
		}
	}
	/**
	 * post request for new group post
	 * @param post 		group post data like group id,content...
	 */
	@RequestMapping("/new_post")
	public void newPost(@RequestBody HashMap<String, String> post) {
		// group where to save new post
		Group group = groupService.getGroup(Long.valueOf(post.get("group_id")));
		// get author entity from database
		User author = userService.getUser(post.get("author"));
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
	 * @param like 		group,post and id
	 */
	@RequestMapping("/add_like")
	public void addLike(@RequestBody HashMap<String, String> like) {
		// group from where post come
		String groupId = like.get("group_id");
		// post where to add like
		String postId  = like.get("post_id");
		// get post likes
		groupService.getPost(groupId,postId).getLikes()
			// add user id to likes
			.add(Long.valueOf(like.get("user_id")));
		// update group containing posts with likes
		groupRepository.save(
			groupService.getGroup(Long.valueOf(groupId))
		);
	}
	/**
	 * post request for removing like from post in group
	 * @param like 		group,post and id
	 */
	@RequestMapping("/remove_like")
	public void removeLike(@RequestBody HashMap<String, String> like) {
		// group from where post come
		String groupId = like.get("group_id");
		// post from where remove like
		String postId  = like.get("post_id");
		// get post likes
		groupService.getPost(groupId,postId).getLikes()
			// remove user id from post likes
			.remove(Long.valueOf(like.get("user_id")));
		// update group containing posts with likes
		groupRepository.save(
			groupService.getGroup(Long.valueOf(groupId))
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
}
