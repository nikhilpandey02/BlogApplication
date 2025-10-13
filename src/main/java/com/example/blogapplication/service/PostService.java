package com.example.blogapplication.service;
import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.Tag;
import com.example.blogapplication.repositories.PostRepository;
import com.example.blogapplication.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.format.DateTimeParseException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    public void savePost(Post post) {
        String tagsString = post.getTagsAsString();

        if (tagsString != null && !tagsString.isEmpty()) {
            String[] tagNames = tagsString.split("#");
            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    Tag tag = tagRepository.findByName(tagName);
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tagRepository.save(tag);
                    }
                    tags.add(tag);
                }
            }
            post.setTags(tags);
        }

        postRepository.save(post);
    }

    public List<Post> getAllPost() {
        return postRepository.findAll();

    }
    public Page<Post> searchPosts(String search, List<String> authors, List<String> tags, List<String> publishedDates, String sort, Pageable pageable) {

        if (search != null && !search.isBlank()) {
            search = search.trim();
            // Change your repository method to return a Page<Post>, accepting Pageable
            return postRepository.findAllByTitleOrAuthorOrTagsOrContent(search, pageable);
        }

        Specification<Post> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (authors != null && !authors.isEmpty()) {
            spec = spec.and(PostSpecification.hasAuthors(authors));
        }

        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(PostSpecification.hasTags(tags));
        }

        if (publishedDates != null && !publishedDates.isEmpty()) {
            List<LocalDate> localDates = publishedDates.stream()
                    .map(LocalDate::parse)
                    .collect(Collectors.toList());

            spec = spec.and(PostSpecification.hasPublishedDates(localDates));
        }

        // Adjust the sorting as part of Pageable or create new pageable with sorting
        Sort sortObj = Sort.by("publishedDate");
        if ("asc".equalsIgnoreCase(sort)) {
            sortObj = sortObj.ascending();
        } else {
            sortObj = sortObj.descending();
        }
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

        return postRepository.findAll(spec, sortedPageable);
    }

    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getById(Integer id) {
        Optional<Post> post= postRepository.findById(id);
        if(post.isPresent())
        {
            return post.get();
        }else
            return null;
    }
    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }

    public void addComment(Integer postId, String content) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Comment comment=new Comment();
            comment.setComment(content);
            comment.setPost(post);
            List<Comment> listComments= post.getComments();
            if(listComments==null)
            {
                listComments=new ArrayList<>();
            }
            listComments.add(comment);
            post.setComments(listComments);
            postRepository.save(post);
            System.out.println("Adding comment to post ID " + postId + ": " + content);
        } else {
            System.out.println("Post with ID " + postId + " not found.");
        }
    }

//    public List<Post> searchPosts(String search, List<String> authors, List<String> tags, List<String> publishedDates, String sort) {
//
//        if (search != null && !search.isBlank()) {
//            search = search.trim();
//            return postRepository.findAllByTitleOrAuthorOrTagsOrContent(search);
//        }
//
//        Specification<Post> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
//
//        if (authors != null && !authors.isEmpty()) {
//            spec = spec.and(PostSpecification.hasAuthors(authors));  // update spec method for List<String>
//        }
//
//        if (tags != null && !tags.isEmpty()) {
//            spec = spec.and(PostSpecification.hasTags(tags));  // update spec method for List<String>
//        }
//
//        if (publishedDates != null) {
//            List<LocalDate> localDates = publishedDates.stream()
//                    .map(LocalDate::parse)
//                    .collect(Collectors.toList());
//
//            spec = spec.and(PostSpecification.hasPublishedDates(localDates));
//        }
//
//        Sort sorting = Sort.by("publishedDate");
//        if ("asc".equalsIgnoreCase(sort)) {
//            sorting = sorting.ascending();
//        } else {
//            sorting = sorting.descending();
//        }
//
//        return postRepository.findAll(spec, sorting);
//    }


}
