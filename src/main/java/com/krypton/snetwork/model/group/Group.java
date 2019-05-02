package com.krypton.snetwork.model.group;

import com.krypton.snetwork.model.Image;
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
	private Image profilePhoto;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "background_id")
	private Image backgroundPhoto;

	@ManyToMany
	private Set<User> followers = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Post> posts = new HashSet<>();

	@Column
	private EntityType type;

	public Group() {}

	public Group(
			String name,
			User admin,
			Image profilePhoto,
			Image backgroundPhoto
	) {
		this.name 			 = name;
		this.admin 			 = admin;
		this.profilePhoto 	 = profilePhoto;
		this.backgroundPhoto = backgroundPhoto;
		this.type 			 = EntityType.GROUP;
	}
}
