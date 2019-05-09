package com.krypton.snetwork.controllers;

import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.service.image.ImageServiceImpl;
import com.krypton.snetwork.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageServiceImpl imageService;

    @Autowired
    private UserServiceImpl userService;
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
    /**
     * set {@link User} profile picture
     * @param profilePicture
     * @param email             {@link User} email
     */
    @PostMapping("/add/user/profile-picture")
    public void uploadUserProfilePicture(
        @RequestParam("picture") MultipartFile profilePicture,
        @RequestParam("email")   String email
    ) {
        // load picture to database
        Image picture = imageService.insertProfilePicture(email, profilePicture);

        userService.setProfilePicture(email, picture);
    }
    /**
     * set {@link User} background picture
     * @param backgroundPicture
     * @param email             {@link User} email
     */
    @PostMapping("/add/user/background-picture")
    public void uploadUserBackgroundPicture(
        @RequestParam("picture") MultipartFile backgroundPicture,
        @RequestParam("email")   String email
    ) {
        // load background to database
        Image background = imageService.insertBackground(email, backgroundPicture);

        userService.setBackgroundPicture(email, background);
    }
}