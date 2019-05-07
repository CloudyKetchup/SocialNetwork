package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;

public interface UserService {

	/**
	 * check if {@link User} with given email exist
	 * @param email 	{@link User} email
	 * @return boolean
	 */
	boolean userExist(String email);
	/**
	 * add {@link Group} to {@link User} groups list
	 * @param group  	{@link Group}
	 * @param user 		{@link User}
	 */
	void addGroupToUser(Group group, User user);

	void addPost(Long author, Post post);

	/**
	 * get {@link User} by email
	 * @param  email 	{@link User} email
	 * @return {@link User}
	 */
	User getUser(String email);
	/**
	 * get {@link User} by id
	 * @param  id 		{@link User} id
	 * @return {@link User}
	 */
	User getUser(Long id);
	/**
	 * find {@link User} by name
	 * @param name    {@link User} name
	 * @return {@link User}
	 */
	User getUserByName(String name);
	/**
	 * save new {@link User} to database
	 * @param name
	 * @param email
	 * @param password
	 * @param profilePicture
	 */
	void saveUser(String name, String email, String password, Image profilePicture);
}
