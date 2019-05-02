package com.krypton.snetwork.service.image;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.repository.ImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private ImageRepository imageRepository;

	@Override
	public void insertImage(String name, MultipartFile image) {
		try {
			// group image entity
			Image imageEntity = createImage(name, image);
			// save image to database
			imageRepository.save(imageEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insertBackground(String name, MultipartFile background) {
		try {
			// group background entity
			Image imageEntity = createBackground(name, background);
			// save background to database
			imageRepository.save(imageEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Image createImage(String name, MultipartFile image) throws IOException {
		// resize image for faster loading
		byte[] newImage = resizeImage(multipartToFile(image));
		// image entity
		return new Image(
			name + "-photo",
			image.getContentType(),
			newImage
		);
	}

	@Override
	public Image createBackground(String name, MultipartFile background) throws IOException {
		// resize background for faster loading
		byte[] newBackground = resizeBackground(multipartToFile(background));
		// background entity
		return new Image(
			name + "-background",
			background.getContentType(),
			newBackground
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
		return imageRepository.findById(id).get();
	}

	@Override
	public byte[] resizeImage(File image) throws IOException {
		// resize image to given width and height
		BufferedImage resizedImage = Thumbnails.of(image).size(500, 500).asBufferedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(resizedImage, "jpg", outputStream);
		// write bytes to byte array
		return outputStream.toByteArray();
	}

	@Override
	public byte[] resizeBackground(File image) throws IOException {
		// resize image to given width and height
		BufferedImage newImage = Thumbnails.of(image).size(1280,720).asBufferedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(newImage, "jpg", outputStream);
		// write bytes to byte array
		return outputStream.toByteArray();
	}

	private File multipartToFile(MultipartFile file) {
		File fileDir = new File(
				System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename()
		);
		try {
			file.transferTo(fileDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileDir;
	}
}
