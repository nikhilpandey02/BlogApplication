//package com.example.blogapplication.repositories;
//
//import com.example.blogapplication.model.Post;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {
//
////    List<Post> findAll();
////    Optional<Post> findById(Integer id);
//
//
//    @Query(
//            "SELECT p FROM posts p LEFT JOIN p.tags t " +
//                    "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
//                    "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :search, '%')) " +
//                    "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
//                    "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))"
//    )
//     List<Post> findAllByTitleOrAuthorOrTagsOrContent(String search);
//
//    @Query( "SELECT p.* FROM posts p " +
//            "LEFT JOIN post_tags pt ON p.id = pt.post_id " +
//            "LEFT JOIN tags t ON pt.tag_id = t.id " +
//            "WHERE (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
//            "   OR LOWER(p.author) LIKE LOWER(CONCAT('%', :search, '%')) " +
//            "   OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
//            "   OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
//            "AND (:author IS NULL OR p.author = :author) " +
//            "AND (:tag IS NULL OR t.name = :tag) " +
//            "AND (:publishedDate IS NULL OR p.published_date = :publishedDate) " +
//            "ORDER BY CASE WHEN :sort = 'asc' THEN p.published_date END ASC, " +
//            "CASE WHEN :sort = 'desc' THEN p.published_date END DESC")
//    List<Post> searchPostsNative(@Param("search") String search,
//                                 @Param("author") String author,
//                                 @Param("tag") String tag,
//                                 @Param("publishedDate") LocalDate publishedDate,
//                                 @Param("sort") String sort);
////
////    List<String> findDistinctAuthors();
////
////    List<LocalDate> findDistinctPublishedDates();
//}
package com.example.blogapplication.repositories;

import com.example.blogapplication.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {

    @Query(value = "SELECT DISTINCT p.* FROM posts p " +
            "LEFT JOIN posts_tags pt ON p.post_id = pt.posts_post_id " +
            "LEFT JOIN tags t ON pt.tags_id = t.id " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.author) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))",
            nativeQuery = true)
    Page<Post> findAllByTitleOrAuthorOrTagsOrContent(String search, Pageable pageable);
   // List<Post> findAllByTitleOrAuthorOrTagsOrContent(@Param("search") String search);

    // For full text search


    // For specification search with pagination
    Page<Post> findAll(Specification<Post> spec, Pageable pageable);


//    @Query(value = "SELECT DISTINCT p.* FROM posts p " +
//            "LEFT JOIN posts_tags pt ON p.post_id = pt.posts_post_id " +
//            "LEFT JOIN tags t ON pt.tags_id = t.id " +
//            "WHERE (:search IS NULL OR p.title ILIKE CONCAT('%', :search, '%') " +
//            "   OR p.author ILIKE CONCAT('%', :search, '%') " +
//            "   OR t.name ILIKE CONCAT('%', :search, '%') " +
//            "   OR p.content ILIKE CONCAT('%', :search, '%')) " +
//            "AND (:author IS NULL OR p.author = :author) " +
//            "AND (:tag IS NULL OR t.name = :tag) " +
//            "AND (:publishedDate IS NULL OR p.published_at = :publishedDate) " +  // fixed here
//            "ORDER BY CASE WHEN :sort = 'asc' THEN p.published_at END ASC, " +
//            "CASE WHEN :sort = 'desc' THEN p.published_at END DESC",
//            nativeQuery = true)
//    List<Post> searchPostsNative(@Param("search") String search,
//                                 @Param("author") String author,
//                                 @Param("tag") String tag,
//                                 @Param("publishedDate") LocalDate publishedDate,
//                                 @Param("sort") String sort);

}
