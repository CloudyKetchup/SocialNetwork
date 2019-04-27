package com.krypton.snetwork.service.image;

import com.krypton.snetwork.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageService {

	/**
	 * save new image entity to database
	 * @param name 		image entity name
	 * @param image 	image file from request
	 */
	public void insertImage(String name, MultipartFile image);
	/**
	 * save new background to database
	 * @param name 		 image entity name
	 * @param background background file from request
	 */
	public void insertBackground(String name, MultipartFile background);
	/** 
	 * create image entity
	 * @param name  	image entity name
	 * @param image 	image multipart file from request
	 * @throws IOException
	 * @return image entity
	 */
	public Image createImage(String name, MultipartFile image) throws IOException;
	/**
	 * get image entity by name
	 * @param name 		image name
	 * @return image entity
	 */
	public Image getImage(String name);
	/** 
	 * create background entity
	 * @param name  		background entity name
	 * @param background 	background  file from request
	 * @throws IOException
	 * @return background entity
	 */
	public Image createBackground(String name, MultipartFile background) throws IOException;
	/**
	 * get background entity by name
	 * @param name 		background name
	 * @return background entity
	 */
	public Image getBackground(String name);
	/**
	 * get image entity by name
	 * @param id 		image name
	 * @return image entity
	 */
	public Image getImage(Long id);
	/**
	 * resize image for lower file size
	 * @param file 		image file for resize
	 * @param width		image width
	 * @param height 	image height
	 * @throws IOException
	 * @return byte array of image
	 * */
	public byte[] resizeImage(File file,int width, int height) throws IOException;
}