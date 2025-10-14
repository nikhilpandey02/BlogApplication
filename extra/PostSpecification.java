//package com.example.blogapplication.service;
//
//import com.example.blogapplication.model.Post;
//import com.example.blogapplication.model.Tag;
//import jakarta.persistence.criteria.*;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public class PostSpecification {
//
////    public static Specification<Post> hasSearchTerm(String search) {
////        return (root, query, cb) -> {
////            if (search == null || search.isEmpty()) {
////                return cb.conjunction();
////            }
////            String likeSearch = "%" + search.toLowerCase() + "%";
////
////            Expression<String> title = cb.lower(root.get("title"));
////            Expression<String> author = cb.lower(root.get("author"));
////            Expression<String> content = cb.lower(root.get("content"));
////
////
////            Join<Post, Tag> tags = root.joinSet("tags", JoinType.LEFT);
////            Expression<String> tagName = cb.lower(tags.get("name"));
////
////            return cb.or(
////                    cb.like(title, likeSearch),
////                    cb.like(author, likeSearch),
////                    cb.like(content, likeSearch),
////                    cb.like(tagName, likeSearch)
////            );
////        };
////    }
//
//    public static Specification<Post> hasAuthors(List<String> authors) {
//        return (root, query, cb) -> {
//            if (authors == null || authors.isEmpty()) {
//                return cb.conjunction();
//            }
//            return root.get("author").in(authors);
//        };
//    }
//
//    public static Specification<Post> hasTags(List<String> tags) {
//        return (root, query, cb) -> {
//            if (tags == null || tags.isEmpty()) {
//                return cb.conjunction();
//            }
//            Join<Post, Tag> tagJoin = root.joinSet("tags", JoinType.LEFT);
//
//            return tagJoin.get("name").in(tags);
//        };
//    }
//
//    public static Specification<Post> hasPublishedDates(List<LocalDate> dates) {
//        return (root, query, cb) -> {
//            if (dates == null || dates.isEmpty()) {
//                return cb.conjunction();
//            }
//            return root.get("publishedDate").in(dates);
//        };
//    }
//
//}

//    public Page<Post> searchPosts(String search, List<String> authors, List<String> tags, List<String> publishedDates, String sort, Pageable pageable) {
//
//        if (search != null && !search.isBlank()) {
//            search = search.trim();
//            return postRepository.findAllByTitleOrAuthorOrTagsOrContent(search, pageable);
//        }
//
//        Specification<Post> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//
//        if (authors != null && !authors.isEmpty()) {
//            spec = spec.and(PostSpecification.hasAuthors(authors));
//        }
//
//        if (tags != null && !tags.isEmpty()) {
//            spec = spec.and(PostSpecification.hasTags(tags));
//        }
//
//        if (publishedDates != null && !publishedDates.isEmpty()) {
//            List<LocalDate> localDates = publishedDates.stream()
//                    .map(LocalDate::parse)
//                    .collect(Collectors.toList());
//
//            spec = spec.and(PostSpecification.hasPublishedDates(localDates));
//        }
//
//        Sort sortObj = Sort.by("publishedDate");
//        if ("asc".equalsIgnoreCase(sort)) {
//            sortObj = sortObj.ascending();
//        } else {
//            sortObj = sortObj.descending();
//        }
//        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);
//
//        return postRepository.findAll(spec, sortedPageable);
//    }

