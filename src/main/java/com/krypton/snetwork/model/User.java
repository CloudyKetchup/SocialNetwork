package com.krypton.snetwork.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
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
    @Transient
    public static Boolean loggedIn = false;

    public User(){}

    public User(String username, String email, String password){
        this.username = username;
        this.email    = email;
        this.password = password;
    }
}
