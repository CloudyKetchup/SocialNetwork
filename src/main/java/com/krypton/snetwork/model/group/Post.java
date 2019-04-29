package com.krypton.snetwork.model.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krypton.snetwork.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter@Setter
@Entity
@Table(name = "Posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long time;

    @Column
    private String content;

    @Column
    @ElementCollection
    private Set<Long> likes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private User author;

    public Post() {}

    public Post(String content,User author,Long time) {
        this.content = content;
        this.author  = author;
        this.time    = time;
    }
}
