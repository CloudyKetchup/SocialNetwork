package com.krypton.snetwork.model.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krypton.snetwork.model.common.Post;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.user.User;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Groups")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name  = "admin_id")
	private User admin;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "image_id")
	private Image profilePicture;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "background_id")
	private Image backgroundPicture;

	@ManyToMany
	@JsonIgnore
	private Set<User> followers = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Post> posts = new HashSet<>();

	@Column
	private EntityType type = EntityType.GROUP;

	public Group() {}

	public Group(String name, User admin, Image profilePicture, Image backgroundPicture) {
		this.name 			 	= name;
		this.admin 			 	= admin;
		this.profilePicture 	= profilePicture;
		this.backgroundPicture 	= backgroundPicture;
		followers.add(admin);
	}

	public Group(String name,User admin) {
		this.name  = name;
		this.admin = admin;
		followers.add(admin);
	}
}
