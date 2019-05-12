package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.*;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.group.GroupServiceImpl;
import com.krypton.snetwork.service.post.PostServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import com.krypton.snetwork.repository.PostRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@RestController
@RequestMapping("/post")
public class PostController {

	private final PostServiceImpl postService;

	private final UserServiceImpl userService;

	private final GroupServiceImpl groupService;

	private final PostRepository postRepository;

	public PostController(PostServiceImpl postService, UserServiceImpl userService, GroupServiceImpl groupService, PostRepository postRepository) {
		this.postService 	= postService;
		this.userService 	= userService;
		this.groupService 	= groupService;
		this.postRepository = postRepository;
	}

	/**
	 * add new {@link Post} to {@link Group} or {@link User} wall
	 *
	 * @param request 		{@link Post} data
	 * @return {@link Post} added to {@link User} or {@link Group}
	 */
	@PostMapping("/new")
	public Post newPost(@RequestBody HashMap<String, String> request) {
		User author = userService.getUser(Long.valueOf(request.get("author")));

		Post post = postService.createPost(
				request.get("content"),				// post text
				author,								// post author
				Long.valueOf(request.get("time"))	// when post was created
		);
		postRepository.save(post);
		// check if post go to group wall else go to user wall
		if (EntityType.GROUP.equalsType(request.get("post_type"))) {
			groupService.addPost(Long.valueOf(request.get("for")), post);
		}else {
			userService.addPost(Long.valueOf(request.get("for")), post);
		}
		return post;
	}
	/**
	 * get {@link Post} author
	 *
	 * @param id 			{@link Post} id
	 * @return {@link Post} author from database
	 */
	@GetMapping("/author/{id:.+}")
	public User postAuthor(@PathVariable("id") Long id) {
	    return postService.getPost(id).getAuthor();
	}
	/**
	 * add picture to {@link Post}
	 *
	 * @param postPicture 	picture for {@link Post}
	 * @param postId 		{@link Post} id
	 */
	@PostMapping("/add/picture")
	public void addPostPicture(
		@RequestParam("post_picture")	MultipartFile postPicture,
		@RequestParam("post")		  	Long postId
	) {
		// add picture to post
		postService.addPostPicture(postPicture,postId);
	}
	/**
	 * get {@link Post} {@link Image},will come on client side like resource
	 *
	 * @param id 			{@link Post} id
	 * @return {@link Post} picture
	 */
	@GetMapping("/picture/{id:.+}")
	public ResponseEntity<byte[]> getPostPicture(@PathVariable("id") Long id) {
		byte[] image = postService.getPost(id)
				.getPicture().getBytes();
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
	}
	/**
	 * add like to {@link Post}
	 *
	 * @param request 		{@link User} and {@link Post} id's
	 */
	@PostMapping("/add/like")
	public void addLike(@RequestBody HashMap<String, Long> request) {
		postService.addLike(
				request.get("post_id"),
				request.get("author_id")
		);
	}
	/**
	 * remove like from {@link Post}
	 *
	 * @param request 		{@link User} and {@link Post} id's
	 */
	@PostMapping("/remove/like")
	public void removeLike(@RequestBody HashMap<String, Long> request) {
		postService.removeLike(
				request.get("post_id"),
				request.get("author_id")
		);
	}
	/**
	 * add new {@link Post} {@link Comment}
	 *
	 * @param request		 {@link Comment} content,{@link Post} id,{@link User} id 
	 * @return updated comments list
	 */
	@PostMapping("/add/comment")
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
