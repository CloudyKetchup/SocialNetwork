package com.krypton.snetwork.service.post;

import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.common.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

public interface PostService {
    /**
     * add new {@link Post} to {@link Group} wall
     * @param content 	{@link Post} text
     * @param author 	{@link Post} author
     * @param time 		time {@link Post} was created
     * @param group 	{@link Group} id
     */
    void newGroupPost(String content, Long author, Long group, Long time);
    /**
     * add new {@link Post} to user wall
     * @param content 	{@link Post} text
     * @param author 	{@link Post} author
     * @param time 		when {@link Post} was created
     */
    void newUserPost(String content, Long author, Long time);
    /**
     * get {@link Post} from database
     * @param id 		{@link Post} id
     * @return {@link Post}
     */
    Post getPost(Long id);
    /**
     * add picture to {@link Post}
     * @param postPhoto multi part file from request
     * @param postId    {@link Post} id
     */
    void addPostPicture(MultipartFile postPhoto, Long postId);
    /**
     * add picture to {@link Post}
     * @param id        {@link Post} id
     * @param picture   {@link Image} 
     */
    void addPicture(Long id, Image picture);
    /**
     * add like to {@link Post}
     * @param id  		{@link Post} id
     * @param authorId	who like post
     */
    void addLike(Long id, Long authorId);
    /**
     * remove like from {@link Post}
     * @param id  		{@link Post} id
     * @param authorId	who liked {@link Post}
     */
    void removeLike(Long id, Long authorId);
    /**
     * add new {@link Comment} to {@link Post}
     * @param id 		{@link Post} id
     * @param comment 	{@link Comment} to be added
     */
    void addComment(Long id, Comment comment);

}
