package com.krypton.snetwork.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Data
@Entity
@Table(name = "Users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "groups")
    @ElementCollection(targetClass = User.class)
    private Set<Group> groups = new HashSet<>();
    
    @Transient
    public static Boolean loggedIn = false;

    public User(){}

    public User(String username, String email, String password){
        this.username = username;
        this.email    = email;
        this.password = password;
    }
}
