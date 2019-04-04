package com.krypton.snetwork.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    
    public Boolean loggedIn = false;

    private static User instance;

    private User(){}

    public static User getInstance(){
    	if (instance == null) {
    		instance = new User();
    	}
    	return instance;
    }
}
