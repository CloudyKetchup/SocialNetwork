package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.apache.tomcat.util.codec.binary.Base64;

import java.util.*;

@RestController
public class HomeController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupServiceImpl groupService;

	@Autowired
	private ImageServiceImpl imageService;

	@Autowired
	private UserServiceImpl userService;

	/**
	 * post request for getting user groups list
	 * @param request 	user account data
	 * @return user groups entities
	 */
	@RequestMapping("/user_groups")
	public HashMap<String, Object> userGroups(@RequestBody HashMap<String, String> request) {
		// return user entity in json
	    	return new HashMap<>(){{
			put("groups",userService.getUser(request.get("email")).getGroups());
		}};
	}
	/**
	 * post request for getting group data
	 * @param request 	json group data
	 * @return group parameters,profile bytes,posts and members
	 */
	@RequestMapping("/group_data")
	public HashMap<String, Object> groupData(@RequestBody HashMap<String, String> request) {
		// return group entity in json
		return new HashMap<>(){{
			put("group",groupService.getGroup(Long.valueOf(request.get("id"))));
		}};
	}
	/**
	 * get group image in base64 byte format
	 * @param request 	group id
	 * @return group image in base64 format
	 */
	@RequestMapping("/group_image")
    public byte[] groupImage(@RequestBody HashMap<String, String> request) {
		// load group image from database
        byte[] image = groupService.getGroup(Long.valueOf(request.get("id")))
				.getGroupImage().getBytes();
		return Base64.encodeBase64(image);
    }
    /**
	 * get group background in base64 byte format
	 * @param request 	group id
	 * @return group background in base64 format
	 */
	@RequestMapping("/group_background")
    public byte[] groupBackground(@RequestBody HashMap<String, String> request) {
		// load group background from database
        byte[] background = groupService.getGroup(Long.valueOf(request.get("id")))
				.getGroupBackground().getBytes();
		return Base64.encodeBase64(background);
    }
	/**
	 * post request for creating new group
	 * @param image 	 group photo
	 * @param background group background photo
	 * @param name  	 group name
	 * @param admin 	 admin email
	 * @return message "group already exist" or "group created"
	 */
	@RequestMapping("/new_group")
	public HashMap<String, String> newGroup(
		@RequestParam("image") 		MultipartFile image,
		@RequestParam("background") MultipartFile background,
		@RequestParam("name") 		String name,
		@RequestParam("admin") 		String admin
	) {
		// check if room with that name exist
		if (groupService.groupExist(name)) {
			return new HashMap<>(){{
				put("response","group already exist");
			}};
		}else {
			// insert group photo to database
			imageService.insertImage(name, image);
			// insert group background to database
			imageService.insertBackground(name, background);
			// insert group to database 
			groupService.insertGroup(name, admin);
			return new HashMap<>(){{
				put("response","group created");
			}};
		}
	}
	/**
	 * post request for new group post
	 * @param request 		group post data like group id,content...
	 */
	@RequestMapping("/new_post")
	public void newPost(@RequestBody HashMap<String, String> request) {
		// group where to save new post
		Group group = groupService.getGroup(Long.valueOf(request.get("group_id")));
		// get author entity from database
		User author = userService.getUser(request.get("author"));
		// add new post to group
		group.getPosts().add(
			// post entity object
			new Post(request.get("content"),author,Long.valueOf(request.get("time")))
		);
		// update group in database
		groupRepository.save(group);
	}
	/**
	 * get post author photo
	 * @param request 		author email
	 * @return author photo in base64 format
	 */
	@RequestMapping("/post_author_image")
	public byte[] authorImage(@RequestBody HashMap<String, String> request) {
		// author entity
		User author  = userService.getUser(request.get("author"));
		// author image
		byte[] image = author.getProfilePhoto().getBytes();
		return Base64.encodeBase64(image);
	}
	/**
	 * post request for adding like to post in group
	 * @param request 		group,post and id
	 */
	@RequestMapping("/add_like")
	public void addLike(@RequestBody HashMap<String, String> request) {
		// group from where post come
		String groupId = request.get("group_id");
		// post where to add like
		String postId  = request.get("post_id");
		// get post likes
		groupService.getPost(groupId,postId).getLikes()
			// add user id to likes
			.add(Long.valueOf(request.get("author_id")));
		// update group containing posts with likes
		groupRepository.save(
			groupService.getGroup(Long.valueOf(groupId))
		);
	}
	/**
	 * post request for removing like from post in group
	 * @param request 		group,post and id
	 */
	@RequestMapping("/remove_like")
	public void removeLike(@RequestBody HashMap<String, String> request) {
		// group from where post come
		String groupId = request.get("group_id");
		// post from where remove like
		String postId  = request.get("post_id");
		// get post likes
		groupService.getPost(groupId,postId).getLikes()
			// remove user id from post likes
			.remove(Long.valueOf(request.get("author_id")));
		// update group containing posts with likes
		groupRepository.save(
			groupService.getGroup(Long.valueOf(groupId))
		);
	}
	/**
	 * add new post comment
	 * @param request		 group,post and athor id
	 * @return updated comments list
	 */
	@RequestMapping("/new_comment")
	public HashMap<String, Post> newComment(@RequestBody HashMap<String, String> request) {
		// group from where post come
		String groupId 	 = request.get("group_id");
		// post where to add comment
		String postId 	 = request.get("post_id");
		// add new comment to post comments list
		groupService.getPost(groupId,postId).getComments()
			.add(new Comment(
				request.get("content"),		// comment content
				userService.getUser(request.get("author"))	// comment author
			));
		// update group
		groupRepository.save(
			groupService.getGroup(Long.valueOf(groupId))
		);
		return new HashMap<String,Post>(){{
			// return comments from updated group
			put("comments",groupService.getPost(groupId,postId));
		}};
	}
	@RequestMapping("/member_image")
	public byte[] memberImage(@RequestBody HashMap<String,String> request) {
		User member   = userService.getUser(request.get("email"));
		// member image
		byte[] image  = member.getProfilePhoto().getBytes();
		return Base64.encodeBase64(image);
	}
}
