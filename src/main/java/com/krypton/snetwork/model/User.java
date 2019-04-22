package com.krypton.snetwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krypton.snetwork.model.group.Group;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter@Setter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Transient
    public static Boolean loggedIn = false;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image profilePhoto;

    @ManyToMany
    @JoinTable(
            name = "users_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonIgnore
    Set<Group> groups = new HashSet<>();

    public User() {}

    public User(
        String username,
        String email,
        String password,
        Image profilePhoto
    ){
        this.username     = username;
        this.email        = email;
        this.password     = password;
        this.profilePhoto = profilePhoto;
    }
}

