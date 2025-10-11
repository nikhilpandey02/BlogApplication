package com.example.blogapplication.model;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
