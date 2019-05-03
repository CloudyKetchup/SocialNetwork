package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public User getUser(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User getUser(Long id) {
		return userRepository.findById(id).get();
	}

	@Override
	public User searchUser(String username) {
		return userRepository.findByName(username);
	}

	@Override
	public void saveUser(String username, String email, String password, Image image) {
		// save user to database
		userRepository.save(new User(username,email,password,image,null));
	}
}
