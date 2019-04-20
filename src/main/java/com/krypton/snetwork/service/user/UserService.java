package com.krypton.snetwork.service.user;

import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.User;

public interface UserService {

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
}
