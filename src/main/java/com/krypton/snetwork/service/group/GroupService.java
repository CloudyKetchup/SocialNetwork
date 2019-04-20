package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GroupService {

	/**
	 * check if group with given name already exist in database 
	 * @param name 		group name
	 */
	public boolean groupExist(String name);
	/**
	 * save new group entity to database with admin
	 * @param name 		group name
	 * @param email 	admin email
	 */
	public void insertGroup(String name, String email);
	/**
	 * save new image entity to database with image file
	 * @param name 		image entity name
	 * @param image 	image multipart file from request
	 */
	public void insertImage(String name, MultipartFile image);
	/**
	 * create group entity with admin entity and avatar image
	 * @param name 		group name
	 * @param admin 	admin email
	 * @param image 	group avatar image  
	 */
	public Group createGroup(String name, String admin, Image image);
	/** 
	 * create image entity 
	 * @param name  	image entity name
	 * @param image 	image multipart file from request
	 */
	public Image createImage(String name, MultipartFile image) throws IOException;
	/**
	 * add new member to group members list
	 * @param group 	group entity
	 * @param member 	member to be added to group
	 */
	public void saveGroupMember(Group group, User member);
	/**
	 * get group entity by name
	 * @param name 		group name
	 */
	public Group getGroup(String name);
	/**
	 * get group entity by id
	 * @param id 		group id
	 */
	public Group getGroup(Long id);
	/**
	 * get image entity by name
	 * @param name 		image name
	 */
	public Image getImage(String name);
	/**
	 * get post entity by id
	 * @param groupId 	group from where post comes
	 * @param postId 	post id
	 */
    public Post getPost(String groupId, String postId);
}