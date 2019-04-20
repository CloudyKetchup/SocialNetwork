package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.repository.GroupRepository;
import com.krypton.snetwork.repository.ImageRepository;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import javax.imageio.*;
import javax.imageio.stream.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.io.*;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private UserServiceImpl userService;
	
	@Override
	public boolean groupExist(String name) {
		return getGroup(name) != null;
	}
	
	@Override
	public void insertGroup(String name, String email) {
		// get admin entity from database
		User admin  = userService.getUser(email);
		// get group image entity from database
		Image image = getImage(name + "-image");
		// create group entity and return as object
		Group group = createGroup(name, name, image);
		// save new group entity to admin
		saveGroupMember(group, admin);
		// save admin entity to group
		userService.saveMemberGroup(group, admin);
	}	
	
	@Override
	public void insertImage(String name, MultipartFile image) {
		try {
			// group image entity
			Image imageEntity = createImage(name, image);
			// save image to database
			imageRepository.save(imageEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Group createGroup(String name, String admin, Image image) {
		// group entity
		return new Group(name,userService.getUser(admin),image);
	}

	@Override
	public Image createImage(String name, MultipartFile image) throws IOException {
		// image entity
		return new Image(
			name + "-image",			// image file name
			image.getContentType(),		// file type
			image.getBytes()			// file bytes
		);
	}
	
	@Override
	public void saveGroupMember(Group group, User member) {
		group.getMembers().add(member);
		groupRepository.save(group);
	}
	
	@Override
	public Group getGroup(String name) {
		return groupRepository.findByName(name);
	}
	
	@Override
	public Group getGroup(Long id) {
		return groupRepository.findById(id).get();
	}
	
	@Override
	public Image getImage(String name) {
		return imageRepository.findByName(name);
	}
	
	@Override
    public Post getPost(String groupId, String postId) {
    	final Post[] posts = new Post[1];
		// get group where that contains post
		getGroup(Long.valueOf(groupId))
			// find post by id
			.getPosts().forEach(post ->  {
				if (post.getId().equals(Long.valueOf(postId))) {
					posts[0] = post;
				}
			});
		return posts[0];
    }
}