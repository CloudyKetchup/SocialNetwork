package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;

import java.util.Set;

public interface UserService {

	/**
	 * check if {@link User} with given email exist
	 *
	 * @param email 	{@link User} email
	 * @return boolean
	 */
	boolean userExist(String email);
	
	/**
	 * add {@link Post} to {@link User} wall
	 *
	 * @param author 	{@link User} id
	 * @param post 		{@link Post} for wall
	 */
	void addPost(Long author, Post post);

	/**
	 * @param user 		{@link User} who to follow
	 * @param follower 	{@link User} who follows
	 */	
	void followUser(User user, User follower);

	/**
	 * @param user 		{@link User} who is followed
	 * @param follower 	{@link User} who follows
	 */	
	void unFollowUser(User user, User follower);

	/**
	 * <p> Get all posts for {@link User} feed,posts on his wall,posts from {@link Group}'s
	 * and {@link User}'s he follows.
	 * </p>
	 * Will run two separate threads that will collect posts from {@link Group}'s and {@link User}'s,
	 * add them to main hash set containing all {@link Post}'s,when both threads finish their work
	 * return set with {@link Post}'s
	 *
	 * @param user 		{@link User} who request feed posts
	 * @return  all posts for {@link User} feed
	 */
	Set<Post> getFeedPosts(User user);

	/**
	 * get {@link User} by email
	 *
	 * @param  email 	{@link User} email
	 * @return {@link User}
	 */
	
	User getUser(String email);
	
	/**
	 * get {@link User} by id
	 *
	 * @param  id 		{@link User} id
	 * @return {@link User}
	 */
	User getUser(Long id);
	
	/**
	 * find {@link User} by name
	 *
	 * @param name    {@link User} name
	 * @return {@link User}
	 */
	User getUserByName(String name);
	
	/**
	 * save {@link User} to database
	 *
	 * @param name		{@link User} name
	 * @param surname	{@link User} surname
	 * @param email		{@link User} email
	 * @param password	{@link User} password
	 */
	void saveUser(String name, String surname, String email, String password);
	
	/**
	 * get {@link User} from database and set profile pricture with given {@link Image}
	 *
	 * @param email			{@link User} email
	 * @param picture 		{@link Image} for profile picture
	 */
	void setProfilePicture(String email, Image picture);
	
	/**
	 * get {@link User} from database and set background picture with given {@link Image}
	 *
	 * @param email			{@link User} email
	 * @param background 	{@link Image} for background
	 */
	void setBackgroundPicture(String email, Image background);
}
