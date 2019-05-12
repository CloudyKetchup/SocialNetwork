package com.krypton.snetwork.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.common.EntityType;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.common.Post;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;    

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private EntityType type = EntityType.USER;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image profilePicture;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "background_id")
    private Image backgroundPicture;

    @ManyToMany
    @JoinTable(
            name = "users_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    @JsonIgnore
    private Set<Group> groups = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<User> following = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Post> posts = new HashSet<>();

    public User() {}

    public User(String name, String surname, String email, String password) {
        this.name     = name;
        this.surname  = surname;
        this.email    = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id="                   + id   +
                ", name='"              + name     + '\'' +
                ", surname='"           + surname  + '\'' +
                ", email='"             + email    + '\'' +
                ", password='"          + password + '\'' +
                ", type="               + type +
                ", profilePicture="     + profilePicture +
                ", backgroundPicture="  + backgroundPicture +
                '}';
    }
}
