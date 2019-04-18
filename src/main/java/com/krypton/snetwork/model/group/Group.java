package com.krypton.snetwork.model.group;

import com.krypton.snetwork.model.User;
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

	@Column
	private String groupImage;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id")
    private User admin;

	@ManyToMany
	private Set<User> members = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Post> posts = new HashSet<>();

	public Group() {}

	public Group(String name,User admin,String groupImage) {
		this.name  = name;
		this.admin = admin;
		this.groupImage = groupImage;
	}
}
