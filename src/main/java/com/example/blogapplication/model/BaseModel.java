package com.example.blogapplication.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {

    @Column(name="created_at", updatable = false)
    @CreationTimestamp
    LocalDate createdAt;
    @Column(name="updated_at")
    @UpdateTimestamp
    LocalDate updatedAt;
}
