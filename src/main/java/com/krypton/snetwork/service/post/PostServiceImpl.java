package com.krypton.snetwork.service.post;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.PostRepository;
import com.krypton.snetwork.repository.UserRepository;
import com.krypton.snetwork.service.group.GroupService;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public void newGroupPost(String content, Long author, Long group, Long time) {
        Group groupEntity = groupService.getGroup(group);
        // add new post to group
        groupEntity.getPosts().add(
                new Post(
                        content,                        // post text
                        userService.getUser(author),    // post author
                        time,                           // time when post was created
                        EntityType.GROUP
                )
        );
        // update group with new post
        groupRepository.save(groupEntity);
    }

    @Override
    public void newUserPost(String content, Long author, Long time) {
        User user = userService.getUser(author);
        // add new post to user
        user.getPosts().add(
                new Post(
                        content,    // post text
                        user,       // post author
                        time,       // time when post was created
                        EntityType.USER
                )
        );
        // update user with new post
        userRepository.save(user);
    }

    @Override
    public Post getPost(Long id) {
        return postRepository.findById(id).get();
    }

    @Override
    public void addPostPicture(MultipartFile postPhoto, HashMap<String, String> postData) {
        // insert picture to database
        imageService.insertImage(postData.get("id") + "-post",postPhoto);
        // picture for post inserted in database
        Image insertedPicture = imageService.getImage(postData.get("id") + "-post");
        // add picture to post
        addPicture(Long.valueOf(postData.get("id")),insertedPicture);
    }

    @Override
    public void addPicture(Long id, Image picture) {
        Post post = getPost(id);
        post.setPicture(picture);
        postRepository.save(post);
    }

    @Override
    public void addLike(Long id, Long authorId) {
        Post post = getPost(id);
        post.getLikes().add(authorId);
        postRepository.save(post);
    }

    @Override
    public void removeLike(Long id, Long authorId) {
        Post post = getPost(id);
        post.getLikes().remove(authorId);
        postRepository.save(post);
    }

    @Override
    public void addComment(Long id, Comment comment) {
        Post post = getPost(id);
        post.getComments().add(comment);
        postRepository.save(post);
    }
}
