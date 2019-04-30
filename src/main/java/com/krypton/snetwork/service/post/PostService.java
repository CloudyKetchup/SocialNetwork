package com.krypton.snetwork.service.post;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

public interface PostService {
    /**
     * add new post to group wall
     * @param content 	post text
     * @param author 	post author
     * @param time 		time post was created
     * @param group 	group id
     */
    void newGroupPost(String content, Long author, Long group, Long time);
    /**
     * add new post to user wall
     * @param content 	post text
     * @param author 	post author
     * @param time 		when post was created
     */
    void newUserPost(String content, Long author, Long time);
    /**
     * get post entity from database
     * @param id 		post id
     * @return post entity
     */
    Post getPost(Long id);
    /**
     * add picture to post
     * @param postPhoto multi part file from request
     * @param postData  post name and id
     */
    void addPostPicture(MultipartFile postPhoto, HashMap<String, String> postData);
    /**
     * add photo to post
     * @param id        post id
     * @param photo     image entity
     */
    void addPicture(Long id, Image photo);
    /**
     * add like to post
     * @param id  		post id
     * @param authorId	who like post
     */
    void addLike(Long id, Long authorId);
    /**
     * remove like from post
     * @param id  		post id
     * @param authorId	who like post
     */
    void removeLike(Long id, Long authorId);
    /**
     * add new comment to post
     * @param id 		post id
     * @param comment 	comment entity
     */
    void addComment(Long id, Comment comment);

}
