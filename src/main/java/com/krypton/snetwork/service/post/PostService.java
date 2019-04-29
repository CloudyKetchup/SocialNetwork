package com.krypton.snetwork.service.post;

import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Post;

public interface PostService {
    /**
     * add new post to group wall
     * @param content 	post text
     * @param author 	post author
     * @param time 		time post was created
     * @param group 	group id
     */
    public void newGroupPost(String content, Long author, Long group, Long time);
    /**
     * add new post to user wall
     * @param content 	post text
     * @param author 	post author
     * @param time 		when post was created
     */
    public void newUserPost(String content, Long author, Long time);
    /**
     * get post entity from database
     * @param id 		post id
     * @return post entity
     */
    public Post getPost(Long id);
    /**
     * add like to post
     * @param id  		post id
     * @param authorId	who like post
     */
    public void addLike(Long id, Long authorId);
    /**
     * remove like from post
     * @param id  		post id
     * @param authorId	who like post
     */
    public void removeLike(Long id, Long authorId);
    /**
     * add new comment to post
     * @param id 		post id
     * @param comment 	comment entity
     */
    public void addComment(Long id,Comment comment);

}
