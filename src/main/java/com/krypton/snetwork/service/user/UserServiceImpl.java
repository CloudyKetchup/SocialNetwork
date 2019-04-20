package com.krypton.snetwork.service.user;

import com.krypton.snetwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void saveMemberGroup(Group group, User member) {
		// add group to member groups list
		member.getGroups().add(group);
		// update member
		userRepository.save(member);
	}

	@Override
	public User getUser(String email) {
		// user entity
		return userRepository.findByEmail(email);
	}
}
