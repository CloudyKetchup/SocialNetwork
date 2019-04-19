package com.krypton.snetwork.model;

import com.krypton.snetwork.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter@Setter
@Entity
@Table(name = "images")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String name;

	@Column
  	private String type;

  	@Lob
    @Column
    private byte[] image;

    public Image() {}

    public Image(String name, String type, byte[] image) {
    	this.name = name;
    	this.type = type;
    	this.image  = image;
    }
}