package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.*;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.common.Tools;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.image.ImageServiceImpl;
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
	private ImageServiceImpl imageService;

	@Autowired
	private Tools tools;

	/**
	 * get {@link User} feed {@link Post}'s,from groups and users who this user follows
	 * @param id 			user id
	 * @return list of posts entities
	 */
	@GetMapping("/feed_posts/{id:.+}")
	public LinkedList<Post> feedPosts(@PathVariable("id") Long id) {
		// account requesting feed posts
		User user = userService.getUser(id);

		LinkedList<Post> feedPosts = new LinkedList<>(user.getPosts());
		// all posts from groups user follows
		for (Group group : user.getGroups()) {
			feedPosts.addAll(group.getPosts());
		}
		return feedPosts;
	}
	/**
	 * add new {@link Post} to {@link Group} or {@link User} wall
	 * @param request 		{@link Post} data (content,author,time...)
	 * @return {@link Post} added to {@link User} or {@link Group}
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
	 * get {@link Post} author
	 * @param id 			{@link Post} id
	 * @return {@link Post} author from database
	 */
	@GetMapping("/post_author/{id:.+}")
	public User postAuthor(@PathVariable("id") Long id) {
	    return postService.getPost(id).getAuthor();
	}
	/**
	 * add picture to {@link Post}
	 * @param postPicture 	picture for {@link Post}
	 * @param postId 		{@link Post} id
	 */
	@PostMapping("/add_post_picture")
	public void addPostPicture(
		@RequestParam("post_picture")	MultipartFile postPicture,
		@RequestParam("post")		  	Long postId
	) {
		// add picture to post
		postService.addPostPicture(postPicture,postId);
	}
	/**
	 * get {@link Post} {@link Image},will come on client side like resource
	 * @param id  		{@link Post} id
	 * @return {@link Post} picture
	 */
	@GetMapping("/post/picture/{id:.+}")
	public ResponseEntity<byte[]> getPostPicture(@PathVariable("id") Long id) {
		byte[] image = postService.getPost(id)
				.getPicture().getBytes();
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
	}
	/**
	 * add like to {@link Post}
	 * @param request 		{@link User} and {@link Post} id's
	 */
	@PostMapping("add_like")
	public void addLike(@RequestBody HashMap<String, Long> request) {
		postService.addLike(
				request.get("post_id"),
				request.get("author_id")
		);
	}
	/**
	 * remove like from {@link Post}
	 * @param request 		{@link User} and {@link Post} id's
	 */
	@PostMapping("remove_like")
	public void removeLike(@RequestBody HashMap<String, Long> request) {
		postService.removeLike(
				request.get("post_id"),
				request.get("author_id")
		);
	}
	/**
	 * add new {@link Post} {@link Comment}
	 * @param request		 {@link Comment} content,{@link Post} id,{@link User} id 
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
