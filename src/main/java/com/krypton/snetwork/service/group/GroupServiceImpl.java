package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.UserRepository;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private UserServiceImpl userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageServiceImpl imageService;
	
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
	public void addFollower(Group group, User member) {
		group.getFollowers().add(member);
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

	@Override
	public void saveProfileAndBackgroundPicture(Group group, MultipartFile profilePhoto, MultipartFile background) {
		// save profile picture for group
		imageService.insertProfilePicture(
				group.getName(), profilePhoto
		);
		// save background picture for group
		imageService.insertBackground(
				group.getName(), background
		);
		// set group profile picture
		group.setProfilePhoto(
				imageService.getProfilePicture(group.getName())
		);
		// set group background picture
		group.setBackgroundPhoto(
				imageService.getBackground(group.getName())
		);
		// update group
		groupRepository.save(group);
	}
}