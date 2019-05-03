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
	boolean userExist(String email);
	/**
	 * add {@link Group} to {@link User} groups list
	 * @param group  	group entity
	 * @param user 		user entity
	 */
	void addGroupToUser(Group group, User user);
	/**
	 * get {@link User} from database by email
	 * @param  email 	user email
	 * @return User entity
	 */
	User getUser(String email);
	/**
	 * get {@link User} from database by id
	 * @param  id 		user id
	 * @return User entity
	 */
	User getUser(Long id);
	/**
	 * find {@link User} in database by name
	 * @param username	{@link User} name
	 * @return {@link User} from database 
	 */
	User searchUser(String username);
	/**
	 * save new {@link User} to database
	 * @param username
	 * @param email
	 * @param password
	 * @param image 	// user avatar bytes
	 */
	void saveUser(String username, String email, String password, Image image);
}
