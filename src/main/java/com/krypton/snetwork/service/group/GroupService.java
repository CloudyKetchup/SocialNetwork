package com.krypton.snetwork.service.group;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.user.User;
import org.springframework.web.multipart.MultipartFile;

public interface GroupService {

	/**
	 * check if {@link Group} with given name already exist in database
	 *
	 * @param name 		{@link Group} name
	 */
	boolean groupExist(String name);

	/**
	 * save new {@link Group} to database
	 *
	 * @param name 			{@link Group} name
	 * @param adminEmail 	admin email
	 * @return {@link Group} that was created
	 */
	Group createGroup(String name, String adminEmail);

	/**
	 * add {@link Group} and {@link User} to each over list,{@link User} to {@link Group}
	 * followers list and {@link Group} tp {@link User} groups list
	 *
	 * @param follower	{@link User} who follows {@link Group}
	 * @param group 	{@link Group} to be followed
	 */
	void followGroup(Group group, User follower);

	/**
	 * remove {@link Group} and {@link User} from each over list's
	 *
     * @param group    {@link Group} to be followed
     * @param follower    {@link User} who follows {@link Group}
     */
	void unFollowGroup(Group group, User follower);

	/**
	 * add new follower to {@link Group}
	 *
	 * @param group 	{@link Group}
	 * @param member 	{@link User} to be added to {@link Group}
	 */
	void addFollower(Group group, User member);

	/**
	 * remove follower from {@link Group}
	 *
	 * @param group 	{@link Group}
	 * @param follower 	{@link User} to be added to {@link Group}
	 */
	void removeFollower(Group group, User follower);

	/**
	 * @param author 	{@link Post} author
	 * @param post 		{@link Post} to be added to {@link Group}
	 */
    
    void addPost(Long author, Post post);
    /**
	 * get {@link Group} by name
	 *
	 * @param name 		{@link Group} name
	 * @return {@link Group} finded by name
	 */
	
	Group getGroup(String name);
	/**
	 * get {@link Group} by id
	 *
	 * @param id 		{@link Group} id
	 * @return {@link Group} finded by id
	 */
	
	Group getGroup(Long id);
	/**
	 * set {@link Group} profile and background {@link Image}
	 *
	 * @param group 			{@link Group}
	 * @param profilePicture	profile picture file
	 * @param background		background picture file
	 */
	void saveProfileAndBackgroundPicture(Group group, MultipartFile profilePicture, MultipartFile background);
}