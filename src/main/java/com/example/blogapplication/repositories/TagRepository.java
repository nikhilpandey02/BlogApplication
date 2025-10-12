package com.example.blogapplication.repositories;

import com.example.blogapplication.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TagRepository extends JpaRepository<Tag, Integer> {

    Tag findByName(String name);


    @Query("SELECT DISTINCT t.name FROM tags t")
    List<String> findDistinctTagNames();

}
