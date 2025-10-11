package com.example.blogapplication.controller;


import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.service.CommentService;
import com.example.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
//@RequestMapping("/blogapplication")
public class DashBoardController {

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @GetMapping("/create-post")
    public String showDashboard(Model model)
    {
        model.addAttribute("post",new Post());
      //  System.out.println("this is dashboard");
        return "create-post";
    }
    @PostMapping("/process-post" )
    public String processPost(Post post,Model model)
    {
        postService.savePost(post);
        return "redirect:/create-post";

    }

    @GetMapping("/homepage")
    public String getPost(Model model)
    {
        List<Post> listOfPost=postService.getAllPost();
        model.addAttribute("posts",listOfPost);
        return "homepage";

    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "search",required = false)String search,
                       @RequestParam(value = "filterBy",required = false)String filter,
                       @RequestParam(value = "sortBy",required = false)String sort,
                       Model model) {
        List<Post> listOfPost=postService.searchPosts(search,filter,sort);
        model.addAttribute("posts",listOfPost);
        return "homepage";


    }

    @GetMapping("/view-post/{postId}")
    public String viewPost(@PathVariable Integer postId,Model model)
    {
      //  System.out.println(postId);
         Post post= postService.getById(postId);
         model.addAttribute("post",post);
         return "viewpost";
    }
    @PostMapping("/update-post/{postId}")
    public String updateData(@PathVariable Integer postId,Model model)
    {

        Post post=postService.getById(postId);
        StringBuilder tagsAsString=new StringBuilder();
        for(var tag:post.getTags())
        {
            tagsAsString.append("#").append(tag.getName()).append(" ");
        }
        post.setTagsAsString(tagsAsString.toString().trim());
        model.addAttribute("post",post);
        return "updatepost";
    }
    @PostMapping("/process-update")
    public String processUpdate(Post post) {
        postService.savePost(post);
        return "redirect:/homepage";
    }
    @PostMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Integer id)
    {
        postService.deletePost(id);
        return "redirect:/homepage";
    }

    @PostMapping("/add-comment/{postId}")
    public String addComment(@PathVariable Integer postId,@RequestParam String content)
    {
       // System.out.println(postId);
        postService.addComment(postId,content);
        return "redirect:/view-post/"+postId;
    }

    @GetMapping("/view-comment/{postId}")
    public String viewComment(@PathVariable Integer postId, Model model) {
        Post post = postService.getById(postId);
        model.addAttribute("post", post);
        return "viewcomments";
    }

    @PostMapping("/edit-comment/{commentId}")
    public String editComment(@PathVariable Integer commentId,
                              Model model, @RequestParam Integer postId) {
        Comment comment= commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        model.addAttribute("postId", postId);
        model.addAttribute("commentId", commentId);
        return "editComment";

    }
    @PostMapping("/delete-comment/{commentId}")
    public String deleteComment(@PathVariable Integer commentId, @RequestParam Integer postId) {
        commentService.deleteComment(commentId);
        return "redirect:/view-post/" + postId;
    }

    @PostMapping("/process-edit-comment")
    public String processEditComment(@RequestParam Integer commentId,
                                     @RequestParam String content,
                                     @RequestParam Integer postId) {
      commentService.saveComment(commentId, content);
        return "redirect:/view-post/" + postId;
    }

}