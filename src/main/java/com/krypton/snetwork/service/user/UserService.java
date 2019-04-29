package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;

public interface UserService {

	/**
	 * check if email already exist in database
	 * @param email 	user email
	 * @return boolean
	 */
	public boolean userExist(String email);
	/**
	 * add group entity to member groups list
	 * @param group  	group entity
	 * @param member 	member entity
	 */
	public void saveMemberGroup(Group group, User member);
	/**
	 * get user entity object from database by email
	 * @param  email 	user email
	 * @return 			User entity
	 */
	public User getUser(String email);
	/**
	 * get user entity object from database by id
	 * @param  id 		user id
	 * @return 			User entity
	 */
	public User getUser(Long id);
	/**
	 * save new user account to database
	 * @param username
	 * @param email
	 * @param password
	 * @param image 	// user avatar bytes
	 */
	public void saveUser(String username, String email, String password,Image image);
}
