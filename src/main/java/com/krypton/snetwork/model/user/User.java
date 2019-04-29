package com.krypton.snetwork.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krypton.snetwork.model.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Post;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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

    @Column
    private EntityType type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image profilePhoto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "background_id")
    private Image backgroundPhoto;

    @ManyToMany
    @JoinTable(
            name = "users_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonIgnore
    private Set<Group> groups = new HashSet<>();

    @ManyToMany
    private Set<User> followers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Post> posts = new HashSet<>();

    public User() {}

    public User(
        String username,
        String email,
        String password,
        Image profilePhoto,
        Image backgroundPhoto
    ){
        this.username        = username;
        this.email           = email;
        this.password        = password;
        this.profilePhoto    = profilePhoto;
        this.backgroundPhoto = backgroundPhoto;
        this.type            = EntityType.USER;
    }
}
