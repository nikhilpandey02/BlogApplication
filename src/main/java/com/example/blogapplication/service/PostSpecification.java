package com.example.blogapplication.service;

import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.Tag;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

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

    public static Specification<Post> hasAuthors(List<String> authors) {
        return (root, query, cb) -> {
            if (authors == null || authors.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("author").in(authors);
        };
    }

    public static Specification<Post> hasTags(List<String> tags) {
        return (root, query, cb) -> {
            if (tags == null || tags.isEmpty()) {
                return cb.conjunction();
            }
            // Join with tags collection
            Join<Post, Tag> tagJoin = root.joinSet("tags", JoinType.LEFT);

            // Create predicate for tags "IN" list
            return tagJoin.get("name").in(tags);
        };
    }

    public static Specification<Post> hasPublishedDates(List<LocalDate> dates) {
        return (root, query, cb) -> {
            if (dates == null || dates.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("publishedDate").in(dates);
        };
    }

}
