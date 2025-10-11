package com.example.blogapplication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity(name="tags")
@Getter
@Setter

public class Tag extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    //change this to list
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;

}
