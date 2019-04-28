package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class PostController {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupServiceImpl groupService;

	@Autowired
	private UserServiceImpl userService;


	/**
	 * post request for new group post
	 * @param request 		group post data like group id,content...
	 */
	@PostMapping("/new_post")
	public void newPost(@RequestBody HashMap<String, String> request) {
		// group where to save new post
		Group group = groupService.getGroup(Long.valueOf(request.get("group_id")));
		// get author entity from database
		User author = userService.getUser(request.get("author"));
		// add new post to group
		group.getPosts().add(
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
	@PostMapping("/post_author_image")
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
	@PostMapping("/add_like")
	public void addLike(@RequestBody HashMap<String, String> request) {
		// group id from where post come
		String groupId = request.get("group_id");
		// post id where to add like
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
	@PostMapping("/remove_like")
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
	@PostMapping("/new_comment")
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
		return new HashMap<>() {{
			put("comments", groupService.getPost(groupId, postId));
		}};
	}
}
