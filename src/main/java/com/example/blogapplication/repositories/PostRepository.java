package com.example.blogapplication.repositories;

import com.example.blogapplication.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {

    List<Post> findAll();
    Optional<Post> findById(Integer id);


    @Query(
            "SELECT p FROM posts p LEFT JOIN p.tags t " +
                    "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))"
    )
     List<Post> findAllByTitleOrAuthorOrTagsOrContent(String search);
//
//    List<String> findDistinctAuthors();
//
//    List<LocalDate> findDistinctPublishedDates();
}
