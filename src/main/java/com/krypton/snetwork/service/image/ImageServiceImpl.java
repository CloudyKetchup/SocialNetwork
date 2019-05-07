package com.krypton.snetwork.service.image;

import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.repository.ImageRepository;
import com.krypton.snetwork.service.common.Tools;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private Tools tools;

	@Override
	public Image getImage(Long id) {
		Optional<Image> image = imageRepository.findById(id);
		// image must be present
		assert  image.isPresent();

		return image.get();
	}

	@Override
	public Image getPostPicture(Post post) {
		return imageRepository.findByName(post.getType() + "-post-" + post.getId()+ "-picture");
	}

	@Override
	public Image getProfilePicture(String name) {
		return imageRepository.findByName(name + "-profile-picture");
	}

	@Override
	public Image getBackground(String name) {
		return imageRepository.findByName(name + "-background-picture");
	}

	@Override
	public void insertPostPicture(Post post, MultipartFile pictureMultipart) {
		Image postPicture = null;
		try {
			 postPicture = createPostPicture(post, pictureMultipart);
		}catch (IOException e) {
			e.printStackTrace();
		}
		assert postPicture != null;

		imageRepository.save(postPicture);
	}

	@Override
	public void insertProfilePicture(String name, MultipartFile pictureMultipart) {
		Image profilePicture = null;
		try {
			profilePicture = createProfilePicture(name, pictureMultipart);
		}catch (IOException e) {
			e.printStackTrace();
		}
		assert profilePicture != null;

		imageRepository.save(profilePicture);
	}

	@Override
	public void insertBackground(String name, MultipartFile backgroundMultipart) {
		Image background = null;
		try {
			background = createBackground(name, backgroundMultipart);
		}catch (IOException e) {
			e.printStackTrace();
		}
		assert background != null;

		imageRepository.save(background);
	}

	@Override
	public Image createPostPicture(Post post, MultipartFile pictureMultipart) throws IOException{
		var width  = 560; 		// post picture max width
		var height = 400;		// post picture max height

		byte[] pictureBytes;

		File pictureFile = tools.multipartToFile(pictureMultipart);

		if(pictureNeedResize(pictureFile, width, height)) {
			pictureBytes = resizePicture(pictureFile, width, height);
		}else {
			pictureBytes = pictureMultipart.getBytes();
		}
		assert pictureBytes != null;

		return new Image(
				post.getType() + "-post-" + post.getId() + "-picture",
				pictureMultipart.getContentType(),
				pictureBytes
		);
	}

	@Override
	public Image createProfilePicture(String name, MultipartFile pictureMultipart) throws IOException {
		var width  = 500; 		// profile picture max width
		var height = 500;		// profile picture max height

		byte[] pictureBytes;

		File pictureFile = tools.multipartToFile(pictureMultipart);

		if(pictureNeedResize(pictureFile, width, height)) {
			pictureBytes = resizePicture(pictureFile, width, height);
		}else {
			pictureBytes = pictureMultipart.getBytes();
		}
		assert pictureBytes != null;

		return new Image(
				name + "-profile-picture",
				pictureMultipart.getContentType(),
				pictureBytes
		);
	}

	@Override
	public Image createBackground(String name, MultipartFile background) throws IOException {
		var width  = 1280; 		// background max width
		var height = 720;		// background max height

		byte[] backgroundBytes;

		File pictureFile = tools.multipartToFile(background);

		if (pictureNeedResize(pictureFile, width, height)) {

			backgroundBytes = resizePicture(pictureFile, width, height);
		}else {
			backgroundBytes = background.getBytes();
		}
		assert backgroundBytes != null;

		return new Image(
				name + "-background-picture",
				background.getContentType(),
				backgroundBytes
		); 
	}

	@Override
	public byte[] resizePicture(File picture, int width , int height) throws IOException {
		// resize picture to given width and height
		BufferedImage updatedPicture = Thumbnails.of(picture).forceSize(width, height).asBufferedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(updatedPicture, "jpg", outputStream);

		return outputStream.toByteArray();
	}

	@Override
	public boolean pictureNeedResize(File picture, int width, int height) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(picture);
		}catch (IOException e) {
			e.printStackTrace();
		}
		assert bufferedImage != null;

		return bufferedImage.getWidth() > width && bufferedImage.getHeight() > height;
	}
}
