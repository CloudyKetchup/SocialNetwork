package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.*;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

	private final UserRepository userRepository;

	private final GroupRepository groupRepository;

	private final PostRepository postRepository;
	
	private final UserServiceImpl userService;

	public GroupServiceImpl(UserRepository userRepository, GroupRepository groupRepository, PostRepository postRepository, UserServiceImpl userService) {
		this.userRepository  = userRepository;
		this.groupRepository = groupRepository;
		this.postRepository  = postRepository;
		this.userService  	 = userService;
	}

	@Override
	public boolean groupExist(String name) {
		return getGroup(name) != null;
	}
	
	@Override
	public Group createGroup(String name, String adminEmail) {
		User admin  = userService.getUser(adminEmail);
		Group group = new Group(name, admin);
		
		admin.getGroups().add(group);

		groupRepository.save(group);
		userRepository.save(admin);

		return group;
	}

	@Override
 	public void followGroup(Group group, User follower) {
 		follower.getGroups().add(group);

 		// add follower to group
 		addFollower(group, follower);

 		// update follower in database
 		userRepository.save(follower);
 	}

 	@Override
 	public void unFollowGroup(Group group, User follower) {
 		follower.getGroups().remove(group);

 		// remove follower from group
 		removeFollower(group, follower);

 		// update follower in database
 		userRepository.save(follower);
 	}

	@Override
	public void addFollower(Group group, User follower) {
		group.getFollowers().add(follower);
		groupRepository.save(group);
	}

	@Override
	public void removeFollower(Group group, User follower) {
		group.getFollowers().remove(follower);
		groupRepository.save(group);
	}

	@Override
	public void addPost(Long groupId, Post post) {
		Group group = getGroup(groupId);
		// add group to post as parent
		post.setGroup(group);
		// update post in database
		postRepository.save(post);
		// add post to group
		group.getPosts().add(post);
		// update group with post
		groupRepository.save(group);
	}
	
	@Override
	public Group getGroup(String name) {
		return groupRepository.findByName(name);
	}
	
	@Override
	public Group getGroup(Long id) {
		Optional<Group> group = groupRepository.findById(id);

		assert group.isPresent();

		return group.get();
	}
}