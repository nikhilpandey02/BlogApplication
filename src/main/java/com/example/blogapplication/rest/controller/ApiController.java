package com.example.blogapplication.rest.controller;

import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.User;
import com.example.blogapplication.service.CommentService;
import com.example.blogapplication.service.PostService;
import com.example.blogapplication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public ApiController(PostService postService,
                               CommentService commentService,
                               UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        boolean success = userService.registerUser(user);
        if (!success) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with that email already exists");
        }
        return ResponseEntity.ok("User registered successfully");
    }


    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userService.findByEmail(auth.getName());
        }
        return null;
    }

    // Get paged posts
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedDate").descending());
        Page<Post> postPage = postService.getPosts(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postPage.getContent());
        response.put("currentPage", postPage.getNumber());
        response.put("totalPages", postPage.getTotalPages());
        response.put("pageSize", postPage.getSize());

        Set<String> authors = new HashSet<>();
        Set<String> tags = new HashSet<>();
        List<LocalDate> dates = new ArrayList<>();

        for (Post p : postPage.getContent()) {
            if (p.getAuthorUser() != null) authors.add(p.getAuthorUser().getName());
            if (p.getPublishedDate() != null) dates.add(p.getPublishedDate());
            for (var t : p.getTags()) tags.add(t.getName());
        }
        response.put("authors", authors);
        response.put("tags", tags);
        response.put("dates", dates);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "author", required = false) List<String> authors,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "publishedDate", required = false) List<String> publishedDates,
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage = postService.searchPosts(search, authors, tags,
                publishedDates, sort, pageable);
        return ResponseEntity.ok(postPage);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> viewPost(@PathVariable Integer postId) {
        Post post = postService.getById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        User loggedInUser = getLoggedInUser();

        if (loggedInUser != null) {
            post.setAuthorUser(loggedInUser);
        }
        postService.savePost(post);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Integer postId, @RequestBody Post post) {
        User loggedInUser = getLoggedInUser();

        Post original = postService.getById(postId);
        if (original == null) {
            return ResponseEntity.notFound().build();
        }

        if (!loggedInUser.hasRole("ROLE_ADMIN") &&
                !original.getAuthorUser().getId().equals(loggedInUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        copyEditablePostFields(post, original);
        postService.savePost(original);
        return ResponseEntity.ok(original);
    }

    private void copyEditablePostFields(Post source, Post target) {
        target.setTitle(source.getTitle());
        target.setExcerpt(source.getExcerpt());
        target.setContent(source.getContent());
        target.setIsPublished(source.getIsPublished());
        target.setPublishedDate(source.getPublishedDate());
        target.setTags(source.getTags());
        target.setTagsAsString(source.getTagsAsString());
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        User loggedInUser = getLoggedInUser();
        Post post = postService.getById(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        if (!loggedInUser.hasRole("ROLE_ADMIN") &&
                (post.getAuthorUser() == null ||
                        !post.getAuthorUser().getId().equals(loggedInUser.getId()))) {
            return ResponseEntity.status(403).build();
        }
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Void> addComment(@PathVariable Integer postId,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) String email,
                                           @RequestParam String content) {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser != null) {
            postService.addComment(postId, loggedInUser.getName(), loggedInUser.getEmail(), content);
        } else {
            postService.addComment(postId, name, email, content);
        }
        return ResponseEntity.noContent().build();
    }

    // Edit comment
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable Integer commentId,
                                               @RequestBody Comment comment) {
        Comment existingComment = commentService.getCommentById(commentId);
        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        }
        commentService.saveComment(commentId, comment.getComment());
        return ResponseEntity.ok(existingComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
