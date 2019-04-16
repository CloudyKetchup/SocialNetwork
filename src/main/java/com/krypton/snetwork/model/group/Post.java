package com.krypton.snetwork.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer likes;

    @OneToMany(cascade = CascadeType.ALL)
    @Column
    private Set<Comment> comments = new HashSet<>();

    @Column
    private String content;

    @OneToOne(cascade = CascadeType.ALL)
    @Column
    private User author;

    public Post() {}

    public Post(String content,User author) {
        this.content = content;
        this.author  = author;
    }
}
