package com.krypton.snetwork.service.image;

import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.repository.ImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@Service
public class ImageServiceImpl implements ImageService{

	@Autowired
	private ImageRepository imageRepository;

	@Override
	public void insertImage(String name, MultipartFile image) {
		try {
			// group bytes entity
			Image imageEntity = createImage(name, image);
			// save bytes to database
			imageRepository.save(imageEntity);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Image createImage(String name, MultipartFile image) throws IOException {
		// resize bytes
//		byte[] resizedImage = resizeImage(multipartToFile(image),800,800);
		// bytes entity
		return new Image(
			name + "-image",		// bytes file name
			image.getContentType(),		// bytes type
			image.getBytes()            // bytes bytes
		);
	}
	
	@Override
	public Image getImage(String name) {
		return imageRepository.findByName(name + "-image");
	}

	@Override
	public Image getImage(Long id) {
		return imageRepository.findById(id).get();
	}

	public File multipartToFile(MultipartFile file) {
		File convFile = new File(
			System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename()
		);
		try {
			file.transferTo(convFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convFile;
	}
	@Override
	public byte[] resizeImage(File image, int width, int height) throws IOException{
		// resize bytes to choosen width and height
		BufferedImage resizedImage = Thumbnails.of(image).size(width,height).asBufferedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(resizedImage, "jpg", outputStream);
		// write bytes to byte array
		return Base64.getEncoder().encode(outputStream.toByteArray());
	}
}