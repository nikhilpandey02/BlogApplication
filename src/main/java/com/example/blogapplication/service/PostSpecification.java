package com.example.blogapplication.service;

import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PostSpecification {

//    public static Specification<Post> hasSearchTerm(String search) {
//        return (root, query, cb) -> {
//            if (search == null || search.isEmpty()) {
//                return cb.conjunction();
//            }
//            String likeSearch = "%" + search.toLowerCase() + "%";
//
//            Expression<String> title = cb.lower(root.get("title"));
//            Expression<String> author = cb.lower(root.get("author"));
//            Expression<String> content = cb.lower(root.get("content"));
//
//            // Join tags to allow searching in tag names
//            Join<Post, Tag> tags = root.joinSet("tags", JoinType.LEFT);
//            Expression<String> tagName = cb.lower(tags.get("name"));
//
//            return cb.or(
//                    cb.like(title, likeSearch),
//                    cb.like(author, likeSearch),
//                    cb.like(content, likeSearch),
//                    cb.like(tagName, likeSearch)
//            );
//        };
//    }

    public static Specification<Post> hasAuthor(String author) {
        return (root, query, cb) -> {
            if (author == null || author.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("author"), author);
        };
    }

    public static Specification<Post> hasTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isEmpty()) {
                return cb.conjunction();
            }
            Join<Post, Tag> tags = root.joinSet("tags", JoinType.LEFT);
            return cb.equal(tags.get("name"), tag);
        };
    }

    public static Specification<Post> hasPublishedDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("publishedDate"), date);
        };
    }
}
