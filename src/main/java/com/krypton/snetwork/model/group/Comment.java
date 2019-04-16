package com.krypton.snetwork.model.group;

import com.krypton.snetwork.model.User;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @OneToOne
    @JoinColumn(name = "author")
    private User author;
}
