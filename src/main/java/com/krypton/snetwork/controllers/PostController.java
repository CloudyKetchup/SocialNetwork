package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.service.post.PostServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class PostController {

	@Autowired
	private UserServiceImpl userService;

	@Autowired
    private PostServiceImpl postService;

	/**
	 * post request for new group post
	 * @param request 		group post data like group id,content...
	 */
	@PostMapping("/new_post")
	public void newPost(@RequestBody HashMap<String, String> request) {
        // post text
	    String content = request.get("content");
        // post author
	    Long author    = Long.valueOf(request.get("author"));
        // post group
	    Long group     = Long.valueOf(request.get("group"));
        // when post was created
	    Long time      = Long.valueOf(request.get("time"));
	    // check if post go to group wall else go to user wall
	    if (EntityType.GROUP.equalsType(request.get("post_type"))){
            postService.newGroupPost(content, author, group, time);
        }else {
            postService.newUserPost(content, author, time);
        }
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
	@PostMapping("group/add_like")
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
	@PostMapping("group/remove_like")
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
