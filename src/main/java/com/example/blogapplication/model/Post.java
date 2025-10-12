package com.example.blogapplication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity(name="posts")
@Getter
@Setter
public class Post extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    private String title;

    private String excerpt;

    private String content;

    private String author;

    @Column(name = "published_at")
    @CreationTimestamp
    private LocalDate publishedDate;

    @Column(name = "is_published")
    private Boolean isPublished;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Tag> tags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Transient
    private String tagsAsString;



}
