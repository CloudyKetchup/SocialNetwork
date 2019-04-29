package com.krypton.snetwork.service.post;

import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.PostRepository;
import com.krypton.snetwork.repository.UserRepository;
import com.krypton.snetwork.service.group.GroupService;
import com.krypton.snetwork.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

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
                        time                            // time when post was created
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
                        time        // time when post was created
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
    public void addLike(Long id, Long authorId) {
        Post post = getPost(id);
        post.getLikes().add(id);
        // update post
        postRepository.save(post);
    }

    @Override
    public void removeLike(Long id, Long authorId) {
        Post post = getPost(id);
        post.getLikes().remove(id);
        // update post
        postRepository.save(post);
    }

    @Override
    public void addComment(Long id, Comment comment) {
        Post post = getPost(id);
        post.getComments().add(comment);
        // update post
        postRepository.save(post);
    }
}
