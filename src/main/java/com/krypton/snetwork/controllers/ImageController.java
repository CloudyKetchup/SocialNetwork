package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageServiceImpl imageService;
	/**
     * get {@link Image},will come on client side like resource
     * @param id 	    {@link Image} id
     * @return {@link Image} entity
     */
    @GetMapping("/{id:.+}")
    public ResponseEntity<byte[]> getGroupImage(@PathVariable("id") Long id) {
        // load group image from database
        byte[] image = imageService.getImage(id).getBytes();
        // return as resource
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}