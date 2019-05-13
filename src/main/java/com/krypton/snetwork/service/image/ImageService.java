package com.krypton.snetwork.service.image;

import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.user.User;
import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.group.Group;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageService {

	/**
	 * @param id 		{@link Image} id
	 * @return {@link Image}
	 */
	Image getImage(Long id);

	/**
	 * @param post		{@link Post}
	 * @return {@link Image}
	 */
	 Image getPostPicture(Post post);

	 /**
	 * get profile picture for {@link User} or {@link Group}
	  *
	 * @param name 		{@link User} or {@link Group} name
	 * @return {@link Image}
	 */
	Image getProfilePicture(String name);

	/**
	 * get background {@link Image} for {@link User} or {@link Group}
	 *
	 * @param name 		{@link User} or {@link Group} name
	 * @return {@link Image}
	 */
	Image getBackground(String name);

	/**
	 * save {@link Image} for {@link Post}
	 *
	 * @param post 				{@link Post}
	 * @param pictureMultipart	{@link Post} picture
	 * @return inserted {@link Image}
	 */
	Image insertPostPicture(Post post, MultipartFile pictureMultipart);

	/**
	 * save profile {@link Image} for {@link User} or {@link Group}
	 *
	 * @param name 					{@link Image} name
	 * @param pictureMultipart 		picture file from request
	 * @return inserted {@link Image}
	 */
	Image insertProfilePicture(String name, MultipartFile pictureMultipart);

	/**
	 * save background {@link Image} for {@link User} or {@link Group}
	 *
	 * @param name 		 		  	{@link Image} name
	 * @param backgroundMultipart 	background from request
	 * @return inserted {@link Image}
	 */
	Image insertBackground(String name, MultipartFile backgroundMultipart);

	/**
	 * create {@link Image} for {@link Post} picture
	 *
	 * @param post 		{@link Post} to witch create picture
	 * @param picture  	picture from request
	 * @throws IOException
	 * @return {@link Image}
	 */
	Image createPostPicture(Post post, MultipartFile picture) throws IOException;

	/**
	 * create {@link Image} for {@link User} or {@link Group} profile picture
	 *
	 * @param name 		{@link User} or {@link Group} name
	 * @param picture	picture from request
	 * @throws IOException
	 * @return {@link Image}
	 */
	Image createProfilePicture(String name, MultipartFile picture) throws IOException;

	/**
	 * create {@link Image} from {@link User} or {@link Group} background picture
	 *
	 * @param name  		{@link User} or {@link Group} name
	 * @param background 	picture from request
	 * @throws IOException
	 * @return {@link Image}
	 */
	Image createBackground(String name, MultipartFile background) throws IOException;

	/**
	 * resize picture resolution for lower file size
	 *
	 * @param file 		 picture file for resize
	 * @throws IOException
	 * @return byte of picture
	 * */
	byte[] resizePicture(File file, int width , int height) throws IOException;

	/**
	 * check if picture have too bigger resolution and need resizing
	 *
	 * @param picture 	{@link Image} for check
	 * @param width 	{@link Image} max width
	 * @param height  	{@link Image} max height
	 * @return boolean
	 */
	boolean pictureNeedResize(File picture, int width, int height);

	/**
	 * set {@link Group} profile and background {@link Image}
	 *
	 * @param group 			{@link Group}
	 * @param profilePicture	profile picture file
	 * @param background		background picture file
	 */
	void saveProfileAndBackgroundPicture(Group group, MultipartFile profilePicture, MultipartFile background);
}