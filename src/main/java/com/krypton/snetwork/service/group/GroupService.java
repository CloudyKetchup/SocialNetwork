package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import org.springframework.web.multipart.MultipartFile;

public interface GroupService {

	/**
	 * check if {@link Group} with given name already exist in database 
	 * @param name 		{@link Group} name
	 */
	boolean groupExist(String name);
	/**
	 * save new {@link Group} to database
	 * @param name 			{@link Group} name
	 * @param adminEmail 	admin email
	 */
	Group createGroup(String name, String adminEmail);
	/**
	 * add new follower to {@link Group} members list
	 * @param group 	{@link Group}
	 * @param member 	{@link User} to be added to {@link Group}
	 */
	void addFollower(Group group, User member);

    void addPost(Long author, Post post);

    /**
	 * get {@link Group} by name
	 * @param name 		{@link Group} name
	 */
	Group getGroup(String name);
	/**
	 * get {@link Group} by id
	 * @param id 		{@link Group} id
	 */
	Group getGroup(Long id);
	/**
	 * set {@link Group} profile and background {@link Image}
	 * @param group 	{@link Group}
	 * @param
	 */
	void saveProfileAndBackgroundPicture(Group group, MultipartFile profilePhoto, MultipartFile background);
}