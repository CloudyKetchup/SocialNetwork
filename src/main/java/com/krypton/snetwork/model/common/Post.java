package com.krypton.snetwork.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.krypton.snetwork.model.group.Group;
import com.krypton.snetwork.model.group.Comment;
import com.krypton.snetwork.model.image.Image;
import com.krypton.snetwork.model.common.EntityType;
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

    @OneToOne(cascade = CascadeType.ALL)
    private Image picture;

    @Column
    private String type;

    @OneToOne(cascade = CascadeType.ALL)
    private Group group;

    public Post() {}

    public Post(String content, User author, Long time) {
        this.content = content;
        this.author  = author;
        this.time    = time;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id="           + id +
                ", time="       + time +
                ", content='"   + content + '\'' +
                ", likes="      + likes +
                ", comments="   + comments +
                ", picture="    + picture +
                ", type="       + type +
                ", group='"     + group + '\'' +
                '}';
    }
}
