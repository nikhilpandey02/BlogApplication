package com.example.blogapplication.service;

import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.Tag;
import com.example.blogapplication.repositories.PostRepository;
import com.example.blogapplication.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<Post> searchPosts(String search, String filter, String sort) {
         List<Post> posts;
        if(search!=null)
        {
            search=search.trim();
           posts= postRepository.findAllByTitleOrAuthorOrTagsOrContent(search);
        }
        else
        {
            posts=postRepository.findAll();
        }
        return posts;

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

}
