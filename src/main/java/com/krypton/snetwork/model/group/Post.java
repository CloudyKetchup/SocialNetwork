package com.krypton.snetwork.model.group;

import com.krypton.snetwork.model.User;
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
    @ElementCollection
    private Set<Long> likes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @JoinColumn(name = "content")
    private String content;

    @Column
    private Long time;

    @OneToOne(cascade = CascadeType.ALL)
    private User author;

    public Post() {}

    public Post(String content,User author,Long time) {
        this.content = content;
        this.author  = author;
        this.time    = time;
    }
}
