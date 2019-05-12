package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final TaskExecutor taskExecutor;

	public UserServiceImpl(UserRepository userRepository, TaskExecutor taskExecutor) {
		this.userRepository = userRepository;
		this.taskExecutor   = taskExecutor;
	}

	@Override
	public boolean userExist(String email) {
		return userRepository.findEmail(email).isPresent();
	}

	@Override
	public void addPost(Long author, Post post) {
		User user = getUser(author);
		user.getPosts().add(post);
		// update user with post
		userRepository.save(user);
	}

 	@Override
 	public void followUser(User user, User follower) {
 		user.getFollowers().add(follower);
 		
 		follower.getFollowing().add(user);

		// update user and follow
 		userRepository.save(user);
 		userRepository.save(follower);
 	}

 	@Override
 	public void unFollowUser(User user, User follower) {
		user.getFollowers().remove(follower);

		follower.getFollowing().remove(user);

		// update user and follow
		userRepository.save(user);
		userRepository.save(follower);
 	}

 	@Override
 	public Set<Post> getFeedPosts(User user) {
 		Set<Post> feedPosts = new HashSet<>(user.getPosts());

    	for (Group group : user.getGroups()) {
			feedPosts.addAll(group.getPosts());
        }
    	for (User following : user.getFollowing()) {
    		feedPosts.addAll(following.getPosts());
       	}
       	return feedPosts;
	}

	@Override
	public User getUser(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User getUser(Long id) {
		Optional<User> user = userRepository.findById(id);

		assert user.isPresent();

		return user.get();
	}

	@Override
	public User getUserByName(String name) {
		return userRepository.findByName(name);
	}

	@Override
	public void saveUser(String name, String surname, String email, String password) {
		userRepository.save(new User(name, surname, email, password));
	}

	@Override
	public void setProfilePicture(String email, Image picture) {
		User user = getUser(email);

		user.setProfilePicture(picture);

		userRepository.save(user);
	}

	@Override
	public void setBackgroundPicture(String email, Image background) {
		User user = getUser(email);

		user.setBackgroundPicture(background);

		userRepository.save(user);	
	}
}
