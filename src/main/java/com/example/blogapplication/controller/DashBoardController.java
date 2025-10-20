package com.example.blogapplication.controller;

import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.User;
import com.example.blogapplication.service.CommentService;
import com.example.blogapplication.service.PostService;
import com.example.blogapplication.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@Controller
public class DashBoardController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public DashBoardController(PostService postService,
                               CommentService commentService,
                               UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/showLoginPage")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user, Model model) {
        boolean success = userService.registerUser(user);
        if (!success) {
            model.addAttribute("error", "User with that name/email already exists");
            return "signup";
        }
        return "redirect:/showLoginPage?signupSuccess";
    }

    @GetMapping("/create-post")
    public String showCreatePost(Model model) {
        Post post = new Post();
        User loggedInUser = getLoggedInUser();
        if (loggedInUser != null) {
            post.setAuthorUser(loggedInUser);
            if (loggedInUser.hasRole("ROLE_ADMIN")) {
                model.addAttribute("users", userService.getAllUsers());
            }
        }
        model.addAttribute("post", post);
        return "createpost";
    }

    @PostMapping("/process-post")
    public String processPost(Post post) {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser != null) post.setAuthorUser(loggedInUser);
        postService.savePost(post);
        return "redirect:/homepage";
    }

    @GetMapping("{"/",/homepage"})
    public String getPosts(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedDate").descending());
        Page<Post> postPage = postService.getPosts(pageable);
        addPostsToModel(model, postPage, page, size);
        return "homepage";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "author", required = false) List<String> authors,
                              @RequestParam(value = "tag", required = false) List<String> tags,
                              @RequestParam(value = "publishedDate", required = false) List<String> publishedDates,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.searchPosts(search, authors, tags, publishedDates, sort, pageable);
        addPostsToModel(model, postPage, page, size);
        return "homepage";
    }

    @GetMapping("/view-post/{postId}")
    public String viewPost(@PathVariable Integer postId,
                           @RequestParam(required = false) Integer editCommentId,
                           Model model) {
        Post post = postService.getById(postId);
        model.addAttribute("post", post);
        model.addAttribute("editCommentId", editCommentId);
        User loggedInUser = getLoggedInUser();
        List<Integer> editableCommentIds = new ArrayList<>();
        if (loggedInUser != null) {
            for (Comment c : post.getComments()) {
                if (loggedInUser.hasRole("ROLE_ADMIN") ||
                        (c.getEmail() != null && c.getEmail().equals(loggedInUser.getEmail()))) {
                    editableCommentIds.add(c.getId());
                }
            }
        }
        model.addAttribute("editableCommentIds", editableCommentIds);
        return "viewpost";
    }

    @PostMapping("/update-post/{postId}")
    public String updatePost(@PathVariable Integer postId, Model model) {
        Post post = postService.getById(postId);
        User loggedInUser = getLoggedInUser();
        if (!loggedInUser.hasRole("ROLE_ADMIN") &&
                !post.getAuthorUser().getId().equals(loggedInUser.getId())) {
            return "redirect:/homepage";
        }
        StringBuilder tagsAsString = new StringBuilder();
        for (var tag : post.getTags()) tagsAsString.append("#").append(tag.getName()).append(" ");
        post.setTagsAsString(tagsAsString.toString().trim());
        model.addAttribute("post", post);
        if (loggedInUser.hasRole("ROLE_ADMIN")) model.addAttribute("users", userService.getAllUsers());
        return "updatepost";
    }

    @PostMapping("/process-update")
    public String processUpdate(Post post) {
        Post originalPost = postService.getById(post.getPostId());
        if (originalPost == null) return "redirect:/homepage";
        copyEditablePostFields(post, originalPost);
        postService.savePost(originalPost);
        return "redirect:/homepage";
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

    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Integer id) {
        Post post = postService.getById(id);
        User loggedInUser = getLoggedInUser();
        if (!loggedInUser.hasRole("ROLE_ADMIN") &&
                (post.getAuthorUser() == null || !post.getAuthorUser().getId().equals(loggedInUser.getId()))) {
            return "redirect:/homepage";
        }
        postService.deletePost(id);
        return "redirect:/homepage";
    }

    @PostMapping("/add-comment/{postId}")
    public String addComment(@PathVariable Integer postId,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String email,
                             @RequestParam String content) {
        User loggedInUser = getLoggedInUser();
        if (loggedInUser != null)
            postService.addComment(postId, loggedInUser.getName(), loggedInUser.getEmail(), content);
        else
            postService.addComment(postId, name, email, content);
        return "redirect:/view-post/" + postId;
    }

    @PostMapping("/edit-comment/{commentId}")
    public String editComment(@PathVariable Integer commentId,
                              @RequestParam Integer postId,
                              Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        model.addAttribute("postId", postId);
        return "editcomment";
    }

    @PostMapping("/process-edit-comment")
    public String processEditComment(@RequestParam Integer id,
                                     @RequestParam String comment,
                                     @RequestParam Integer postId) {
        commentService.saveComment(id, comment);
        return "redirect:/view-post/" + postId;
    }

    @PostMapping("/delete-comment/{commentId}")
    public String deleteComment(@PathVariable Integer commentId, @RequestParam Integer postId) {
        commentService.deleteComment(commentId);
        return "redirect:/view-post/" + postId;
    }

    private void addPostsToModel(Model model, Page<Post> postPage, int currentPage, int pageSize) {
        List<Post> posts = postPage.getContent();
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        Set<String> authors = new HashSet<>();
        Set<String> tags = new HashSet<>();
        Set<LocalDate> dates = new HashSet<>();
        for (Post p : posts) {
            if (p.getAuthorUser() != null) authors.add(p.getAuthorUser().getName());
            if (p.getPublishedDate() != null) dates.add(p.getPublishedDate());
            for (var t : p.getTags()) tags.add(t.getName());
        }
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        model.addAttribute("dates", dates);
    }

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userService.findByEmail(auth.getName());
        }
        return null;
    }
}
