package com.example.blogapplication.service;

import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.Tag;
import com.example.blogapplication.repositories.PostRepository;
import com.example.blogapplication.repositories.TagRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    // --- SAVE OR UPDATE POST ---
    public void savePost(Post post) {
        // Handle tags
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

    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getById(Integer id) {
        return postRepository.findById(id).orElse(null);
    }
    public void addComment(Integer postId, String name, String email, String content) {
        Post post = getById(postId);
        if (post != null) {
            Comment comment = new Comment();
            comment.setName(name);
            comment.setEmail(email);
            comment.setComment(content);
            comment.setPost(post);

            List<Comment> comments = post.getComments();
            if (comments == null) comments = new ArrayList<>();
            comments.add(comment);
            post.setComments(comments);

            postRepository.save(post);
        }
    }

    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }

    public Page<Post> searchPosts(
            String search,
            List<String> authors,
            List<String> tags,
            List<String> publishedDates,
            String sort,
            Pageable pageable
    ) {

        Pageable pageableWithoutSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.unsorted());

        if (search != null && !search.isBlank()) {
            search = search.trim();
            return postRepository.findAllByTitleOrAuthorOrTagsOrContent(search, pageableWithoutSort);
        } else {
            List<LocalDate> publishedDatesLocal = (publishedDates == null ? null :
                    publishedDates.stream()
                            .map(LocalDate::parse)
                            .collect(Collectors.toList()));

            return postRepository.findAllWithFilters(
                    authors, authors == null ? 0 : authors.size(),
                    tags, tags == null ? 0 : tags.size(),
                    publishedDatesLocal, publishedDatesLocal == null ? 0 : publishedDatesLocal.size(),
                    pageableWithoutSort
            );
        }
    }
}
