package com.example.blogapplication.service;


import com.example.blogapplication.model.Comment;
import com.example.blogapplication.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {


    @Autowired
    CommentRepository commentRepository;
    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);

    }

    public void saveComment(Integer commentId, String content) {
        Comment comment = getCommentById(commentId);
        if (comment != null) {
            comment.setComment(content);
            commentRepository.save(comment);
        }
    }

    public Comment getCommentById(Integer commentId) {

        return commentRepository.findById(commentId).orElse(null);
    }
}
