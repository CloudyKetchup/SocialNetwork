package com.krypton.snetwork.service.image;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.repository.ImageRepository;
import com.krypton.snetwork.service.common.Tools;
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
	public void insertProfilePicture(String name, MultipartFile picture) {
		try {
			Image pictureEntity = createProfilePicture(name, picture);
			// save picture to database
			imageRepository.save(pictureEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insertBackground(String name, MultipartFile background) {
		try {
			Image backgroundEntity = createBackground(name, background);
			// save background to database
			imageRepository.save(backgroundEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Image createProfilePicture(String name, MultipartFile image) throws IOException {
		byte[] imageBytes;

		if(profilePictureNeedResize(tools.multipartToFile(image))) {
			imageBytes = resizeProfilePicture(tools.multipartToFile(image));
		}else {
			imageBytes = image.getBytes();
		}
		assert imageBytes != null;

		return new Image(
			name + "-photo",
			image.getContentType(),
			imageBytes
		);
	}

	@Override
	public Image createBackground(String name, MultipartFile background) throws IOException {
		byte[] backgroundBytes;

		if (backgroundNeedResize(tools.multipartToFile(background))) {
			backgroundBytes = resizeBackground(tools.multipartToFile(background));
		}else {
			backgroundBytes = background.getBytes();
		}
		assert backgroundBytes != null;

		return new Image(
			name + "-background",
			background.getContentType(),
			backgroundBytes
		); 
	}

	@Override
	public Image getBackground(String name) {
		return imageRepository.findByName(name + "-background");	
	}

	@Override
	public Image getImage(String name) {
		return imageRepository.findByName(name + "-photo");
	}
	
	@Override
	public Image getImage(Long id) {
		Optional<Image> image = imageRepository.findById(id);
		// image must be present
		assert image.isPresent();

		return image.get();
	}

	@Override
	public byte[] resizeProfilePicture(File picture) throws IOException {
		// resize picture to given width and height
		BufferedImage updatedPicture = Thumbnails.of(picture).size(500, 500).asBufferedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(updatedPicture, "jpg", outputStream);

		return outputStream.toByteArray();
	}

	@Override
	public byte[] resizeBackground(File background) throws IOException {
		// resize background to given width and height
		BufferedImage updatedBackground = Thumbnails.of(background).size(1280,720).asBufferedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(updatedBackground, "jpg", outputStream);

		return outputStream.toByteArray();
	}

	@Override
	public boolean profilePictureNeedResize(File image) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(image);
		}catch (IOException e) {
			e.printStackTrace();
		}
		assert bufferedImage != null;
 
		return bufferedImage.getWidth() > 500 && bufferedImage.getHeight() > 500;
	}

	@Override
	public boolean backgroundNeedResize(File background) {
		BufferedImage bufferedBackground = null;
		try {
			bufferedBackground = ImageIO.read(background);
		}catch (IOException e) {
			e.printStackTrace();
		}
		assert bufferedBackground != null;

		return bufferedBackground.getWidth() > 1280 && bufferedBackground.getHeight() > 720;
	}
}
