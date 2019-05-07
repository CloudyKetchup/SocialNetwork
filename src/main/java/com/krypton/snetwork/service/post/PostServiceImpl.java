package com.krypton.snetwork.service.post;

import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.common.Post;
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
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageServiceImpl imageService;

    @Override
    public Post createPost(String content, User author, Long time) {
        return new Post(content, author, time, EntityType.USER);
    }

    @Override
    public Post getPost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        assert  post.isPresent();

        return post.get();
    }

    @Override
    public void addPostPicture(MultipartFile postPhoto, Long postId) {
        Post post = getPost(postId);
        imageService.insertPostPicture(post, postPhoto);

        Image insertedPicture = imageService.getPostPicture(post);
        // add picture to post
        addPicture(postId,insertedPicture);
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
