package com.example.blogapplication.repositories;
import com.example.blogapplication.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // --- SEARCH BY TITLE / AUTHOR / TAGS / CONTENT ---
    @Query(
            value = "SELECT DISTINCT p.* FROM posts p " +
                    "LEFT JOIN users u ON p.author_id = u.id " +
                    "LEFT JOIN posts_tags pt ON p.post_id = pt.posts_post_id " +
                    "LEFT JOIN tags t ON pt.tags_id = t.id " +
                    "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "ORDER BY p.published_at DESC",
            countQuery = "SELECT COUNT(DISTINCT p.post_id) FROM posts p " +
                    "LEFT JOIN users u ON p.author_id = u.id " +
                    "LEFT JOIN posts_tags pt ON p.post_id = pt.posts_post_id " +
                    "LEFT JOIN tags t ON pt.tags_id = t.id " +
                    "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))",
            nativeQuery = true
    )
    Page<Post> findAllByTitleOrAuthorOrTagsOrContent(
            @Param("search") String search,
            Pageable pageable
    );

    // --- FILTER WITH AUTHORS / TAGS / DATES ---
    @Query(
            value = "SELECT DISTINCT p.* FROM posts p " +
                    "LEFT JOIN users u ON p.author_id = u.id " +
                    "LEFT JOIN posts_tags pt ON p.post_id = pt.posts_post_id " +
                    "LEFT JOIN tags t ON pt.tags_id = t.id " +
                    "WHERE (:authorCount = 0 OR u.name IN (:authors)) " +
                    "AND (:tagCount = 0 OR t.name IN (:tags)) " +
                    "AND (:dateCount = 0 OR p.published_at IN (:publishedDates)) " +
                    "ORDER BY p.published_at DESC",
            countQuery = "SELECT COUNT(DISTINCT p.post_id) FROM posts p " +
                    "LEFT JOIN users u ON p.author_id = u.id " +
                    "LEFT JOIN posts_tags pt ON p.post_id = pt.posts_post_id " +
                    "LEFT JOIN tags t ON pt.tags_id = t.id " +
                    "WHERE (:authorCount = 0 OR u.name IN (:authors)) " +
                    "AND (:tagCount = 0 OR t.name IN (:tags)) " +
                    "AND (:dateCount = 0 OR p.published_at IN (:publishedDates))",
            nativeQuery = true
    )
    Page<Post> findAllWithFilters(
            @Param("authors") List<String> authors,
            @Param("authorCount") int authorCount,
            @Param("tags") List<String> tags,
            @Param("tagCount") int tagCount,
            @Param("publishedDates") List<LocalDate> publishedDates,
            @Param("dateCount") int dateCount,
            Pageable pageable
    );
}
