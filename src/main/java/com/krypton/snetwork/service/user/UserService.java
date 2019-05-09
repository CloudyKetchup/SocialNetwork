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
	/**
	 * add {@link Post} to {@link User} wall
	 * @param author 	{@link User} id
	 * @param post 		{@link Post} for wall
	 */
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
	 * save {@link User} to database
	 * @param name
	 * @param surname
	 * @param email
	 * @param password
	 * @param profilePicture
	 * @param backgroundPicture
	 */
	void saveUser(String name, String surname, String email, String password);
	/**
	 * get {@link User} from database and set profile pricture
	 * with given {@link Image}
	 * @param email			// {@link User} email
	 * @param picture 		// {@link Image} for profile picture
	 */
	void setProfilePicture(String email, Image picture);
	/**
	 * get {@link User} from database and set background pricture
	 * with given {@link Image}
	 * @param email			// {@link User} email
	 * @param background 	// {@link Image} for background
	 */
	void setBackgroundPicture(String email, Image background);
}
