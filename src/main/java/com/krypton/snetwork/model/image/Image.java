package com.krypton.snetwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter@Setter
@Entity
@Table(name = "images")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column
	private String type;

	@Lob
	@Column
	@JsonIgnore
	private byte[] bytes;

	public Image() {}

	public Image(String name, String type, byte[] bytes) {
		this.name 	= name;
		this.type 	= type;
		this.bytes = bytes;
    }
}