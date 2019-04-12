package com.krypton.snetwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "admin_id")
	private User admin;

	@ManyToMany
    @JsonIgnore
	private Set<User> members = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL)
    @JsonIgnore
	private Set<Post> posts = new HashSet<>();

	public Group() {}

	public Group(String name,User admin) {
		this.name  = name;
		this.admin = admin;
	}
	public void addMember(User user) {
		members.add(user);
	}

	public void removeMember(User user) {
	    members.remove(user);
    }

}
