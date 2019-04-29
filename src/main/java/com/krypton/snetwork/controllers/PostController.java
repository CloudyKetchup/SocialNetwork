package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.*;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.post.PostServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.HashMap;
import java.util.LinkedList;

@RestController
public class PostController {

	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private PostServiceImpl postService;
	/**
	 * get posts for user news feed
	 * @param request 		user id
	 * @return list of posts entities
	 */
    @PostMapping("/feed_posts")
    public LinkedList<Post> feedPosts(@RequestBody HashMap<String, Long> request) {
        // account requesting feed posts
        User user = userService.getUser(request.get("user_id"));
        // posts from all groups user follows
        LinkedList<Post> feedPosts = new LinkedList<>();
        // get all user posts
        feedPosts.addAll(user.getPosts());
        // get all group user follows posts
        for (Group group : user.getGroups()) {
            feedPosts.addAll(group.getPosts());
        }
        return feedPosts;
    }
	/**
	 * post request for new group post
	 * @param request 		group post data like group id,content...
	 */
	@PostMapping("/new_post")
	public String newPost(@RequestBody HashMap<String, String> request) {
		// post text
		String content = request.get("content");
		// post author
		Long author    = Long.valueOf(request.get("author"));
		// when post was created
		Long time      = Long.valueOf(request.get("time"));
		// check if post go to group wall else go to user wall
		if (EntityType.GROUP.equalsType(request.get("post_type"))){
            // post group
            Long group = Long.valueOf(request.get("for"));
			postService.newGroupPost(content, author, group, time);
			return "group post added";
		}else {
			postService.newUserPost(content, author, time);
			return "user post added";
		}
	}
	/**
	 * get post author entity
	 * @param request 		post id
	 * @return post author object from database
	 */
	@PostMapping("/post_author")
    public HashMap<String, User> postAuthor(@RequestBody HashMap<String, Long> request) {
	    return new HashMap<>(){{
	        put("author",postService.getPost(request.get("post_id")).getAuthor());
        }};
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
	public HashMap<String, Post> newComment(@RequestBody HashMap<String, String> request) {
		// post where to add comment
		Long postId  = Long.valueOf(request.get("post_id"));
		// add new comment to post
		postService.addComment(
				postId,
				new Comment(
					request.get("content"),
					userService.getUser(request.get("author"))
				)
		);
		return new HashMap<>(){{
			put("post", postService.getPost(postId));
		}};
	}
}
