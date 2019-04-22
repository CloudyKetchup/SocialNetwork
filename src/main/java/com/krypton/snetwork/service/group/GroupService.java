package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import com.krypton.snetwork.model.User;
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
	 * create group entity with admin entity and avatar bytes
	 * @param name 		group name
	 * @param admin 	admin email
	 * @param image 	group avatar bytes
	 */
	public Group createGroup(String name, User admin, Image image);
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
	 * get post entity by id
	 * @param groupId 	group from where post comes
	 * @param postId 	post id
	 */
	public Post getPost(String groupId, String postId);
}