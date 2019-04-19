package com.krypton.snetwork.model.group;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.krypton.snetwork.model.User;
import com.krypton.snetwork.model.Image;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter@Setter
@Entity
@Table(name = "Groups")
public class Group {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false)
	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image groupImage;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name  = "admin_id")
    private User admin;

	@ManyToMany
	private Set<User> members = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Post> posts = new HashSet<>();

	public Group() {}

	public Group(String name,User admin,Image groupImage) {
		this.name  = name;
		this.admin = admin;
		this.groupImage = groupImage;
	}
}
