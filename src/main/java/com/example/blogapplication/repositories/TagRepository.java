package com.example.blogapplication.repositories;

import com.example.blogapplication.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Tag findByName(String name);
}
