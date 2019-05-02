package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.*;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
import com.krypton.snetwork.service.common.Tools;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.post.PostServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

@RestController
public class PostController {

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private PostServiceImpl postService;

	@Autowired
	private GroupServiceImpl groupService;

	@Autowired
	private Tools tools;

	/**
	 * get user feed posts
	 * @param request 		user id
	 * @return list of posts entities
	 */
	@PostMapping("/feed_posts")
	public LinkedList<Post> feedPosts(@RequestBody HashMap<String, Long> request) {
		// account requesting feed posts
		User user = userService.getUser(request.get("user_id"));
		// all user posts
		LinkedList<Post> feedPosts = new LinkedList<>(user.getPosts());
		// all posts from groups user follows
		for (Group group : user.getGroups()) {
			feedPosts.addAll(group.getPosts());
		}
		return feedPosts;
	}
	/**
	 * add new post to group or user wall
	 * @param request 		post data (content,author,time...)
	 * @return post added to user or group
	 */
	@PostMapping("/new_post")
	public Post newPost(@RequestBody HashMap<String, String> request) {
		String content 	 = request.get("content");
		Long author    	 = Long.valueOf(request.get("author"));
		Long time      	 = Long.valueOf(request.get("time"));
		// entity where to add post (group or user)
		Long destination = Long.valueOf(request.get("for"));
		// posts list where post was added
		Set<Post> posts;
		// check if post go to group wall else go to user wall
		if (EntityType.GROUP.equalsType(String.valueOf(request.get("post_type")))){
			// add post to group
			postService.newGroupPost(
				content,				// post text
				author,					// post author
				destination,			// group id
				time					// when post was created
			);
			posts = groupService.getGroup(destination).getPosts();
		}else {
			// add post to user
			postService.newUserPost(
				content,				// post text
				author,					// post author
				time					// when post was created
			);
			posts = userService.getUser(author).getPosts();
		}
		return tools.getLastElement(posts);
	}
	/**
	 * get post author entity
	 * @param id 			post id
	 * @return post author object from database
	 */
	@GetMapping("/post_author/{id:.+}")
	public User postAuthor(@PathVariable("id") Long id) {
	    return postService.getPost(id).getAuthor();
	}
	/**
	 * get post author photo
	 * @param email 		author email
	 * @return author photo in base64 format
	 */
	@GetMapping("/user/profile_picture/{email:.+}")
	public ResponseEntity<byte[]> getAuthorImage(@PathVariable("email") String email) {
		// author entity
		User author  = userService.getUser(email);

		byte[] image = author.getProfilePhoto().getBytes();

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
	}
	/**
	 * add picture to post
	 * @param postPicture 	picture to be added to post
	 * @param postData 		post name and id
	 */
	@PostMapping("/add_post_picture")
	public void addPostPicture(
		@RequestParam("post_picture")	MultipartFile postPicture,
		@RequestParam("post")		  	String postData
	) {
		// parse json from string
		HashMap<String, String> parsedPostData = tools.stringToHashMap(postData);
		// add picture to post
		postService.addPostPicture(postPicture,parsedPostData);
	}
	/**
	 * get post picture,will come on client side like resource
	 * @param id  		post id
	 * @return picture in response body
	 */
	@GetMapping("/post/picture/{id:.+}")
	public ResponseEntity<byte[]> getPostPicture(@PathVariable("id") Long id) {
		byte[] image = postService.getPost(id)
				.getPicture().getBytes();
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
	}
	/**
	 * post request for adding like to post in group
	 * @param request 		group,post and id
	 */
	@PostMapping("add_like")
	public void addLike(@RequestBody HashMap<String, Long> request) {
		postService.addLike(
				request.get("post_id"),
				request.get("author_id")
		);
	}
	/**
	 * post request for removing like from post in group
	 * @param request 		group,post and id
	 */
	@PostMapping("remove_like")
	public void removeLike(@RequestBody HashMap<String, Long> request) {
		postService.removeLike(
				request.get("post_id"),
				request.get("author_id")
		);
	}
	/**
	 * add new post comment
	 * @param request		 group,post and author id
	 * @return updated comments list
	 */
	@PostMapping("/new_comment")
	public Post newComment(@RequestBody HashMap<String, String> request) {
		// post where to add comment
		Long postId = Long.valueOf(request.get("post_id"));
		// add new comment to post
		postService.addComment(
				postId,
				new Comment(
						request.get("content"),
						userService.getUser(request.get("author"))
				)
		);
		return postService.getPost(postId);
	}
}
