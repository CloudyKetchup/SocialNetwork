package com.krypton.snetwork.model;

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

	@Column(nullable = false, unique = true)
	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@Column
	private User admin;

	@ManyToMany
	private Set<User> members = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
	private Set<Post> posts = new HashSet<>();

	public Group() {}

	public Group(String name,User admin) {
		this.name  = name;
		this.admin = admin;
	}
}
