package com.example.blogapplication.controller;

import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.service.CommentService;
import com.example.blogapplication.service.PostService;
import com.example.blogapplication.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class DashBoardController {

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    TagService tagService;

    @GetMapping("/create-post")
    public String showDashboard(Model model) {
        model.addAttribute("post", new Post());
        return "createpost";
    }

    @PostMapping("/process-post")
    public String processPost(Post post) {
        postService.savePost(post);
        return "redirect:/create-post";
    }


    @GetMapping("/homepage")
    public String getPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getPosts(pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("pageSize", size);

        addDropdownDataToModel(postPage.getContent(), model);

        return "homepage";
    }
    @GetMapping("/search")
    public String search(@RequestParam(value = "search", required = false) String search,
                         @RequestParam(value = "author", required = false) List<String> author,
                         @RequestParam(value = "tag", required = false) List<String> tag,
                         @RequestParam(value = "publishedDate", required = false) List<String> publishedDate,
                         @RequestParam(defaultValue = "desc") String sort,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> filteredPostsPage = postService.searchPosts(search, author, tag, publishedDate, sort, pageable);

        model.addAttribute("posts", filteredPostsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", filteredPostsPage.getTotalPages());
        model.addAttribute("pageSize", size);

        addDropdownDataToModel(filteredPostsPage.getContent(), model);

        return "homepage";
    }

@GetMapping("/view-post/{postId}")
public String viewPost(@PathVariable Integer postId,
                       @RequestParam(required = false) Integer editCommentId,
                       Model model) {
    Post post = postService.getById(postId);
    model.addAttribute("post", post);
    model.addAttribute("editCommentId", editCommentId);
    return "viewpost";
}


    @PostMapping("/update-post/{postId}")
    public String updateData(@PathVariable Integer postId, Model model) {
        Post post = postService.getById(postId);
        StringBuilder tagsAsString = new StringBuilder();
        for (var tag : post.getTags()) {
            tagsAsString.append("#").append(tag.getName()).append(" ");
        }
        post.setTagsAsString(tagsAsString.toString().trim());
        model.addAttribute("post", post);
        return "updatepost";
    }

    @PostMapping("/process-update")
    public String processUpdate(Post post) {
        postService.savePost(post);
        return "redirect:/homepage";
    }

    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return "redirect:/homepage";
    }

//    @PostMapping("/add-comment/{postId}")
//    public String addComment(@PathVariable Integer postId, @RequestParam String content) {
//        postService.addComment(postId, content);
//        return "redirect:/view-post/" + postId;
//    }
@PostMapping("/add-comment/{postId}")
public String addComment(@PathVariable Integer postId,
                         @RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String content) {

    if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
        // Early exit on invalid input - no further processing
        return "redirect:/view-post/" + postId;
    }

    // Only if validation passes, add comment and return
    postService.addComment(postId, name, email, content);
    return "redirect:/view-post/" + postId;
}


    @PostMapping("/edit-comment/{commentId}")
    public String editComment(@PathVariable Integer commentId,
                              @RequestParam Integer postId, Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        model.addAttribute("postiding", postId);
        model.addAttribute("commentId", commentId);
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

    private void addDropdownDataToModel(List<Post> posts, Model model) {
        List<String> authors = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();
        Set<String> listOfTags = new HashSet<>();
        for (var post : posts) {
            if (!authors.contains(post.getAuthor())) {
                authors.add(post.getAuthor());
            }
            if (post.getPublishedDate() != null && !dates.contains(post.getPublishedDate())) {
                dates.add(post.getPublishedDate());
            }
        }
        for(var Tag:posts) {
            for (var t : Tag.getTags()) {
                listOfTags.add(t.getName());
            }
        }

        model.addAttribute("authors", authors);
        model.addAttribute("dates", dates);
        model.addAttribute("tags", listOfTags);
    }
}
