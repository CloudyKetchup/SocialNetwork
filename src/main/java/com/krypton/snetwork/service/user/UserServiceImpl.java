package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean userExist(String email) {
		return userRepository.findEmail(email).isPresent();
	}

	@Override
	public void addGroupToUser(Group group, User user) {
		// add group to user groups list
		user.getGroups().add(group);
		// update user
		userRepository.save(user);
	}

	@Override
	public void addPost(Long author, Post post) {
		User user = getUser(author);
		user.getPosts().add(post);
		// update user with post
		userRepository.save(user);
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
