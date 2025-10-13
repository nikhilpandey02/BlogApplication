package com.example.blogapplication.service;
import com.example.blogapplication.model.Comment;
import com.example.blogapplication.model.Post;
import com.example.blogapplication.model.Tag;
import com.example.blogapplication.repositories.PostRepository;
import com.example.blogapplication.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

    public List<Post> searchPosts(String search, String author,String tag,String publishedDate, String sort) {
         List<Post> posts;
        if(search!=null)
        {
            search=search.trim();
           return postRepository.findAllByTitleOrAuthorOrTagsOrContent(search);
        }
//        else
//        {
//            posts=postRepository.findAll();
//        }
//        return posts;
        else {
            Specification<Post> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

//            if (search != null && !search.isBlank()) {
//                spec = spec.and(PostSpecification.hasSearchTerm(search.trim()));
//            }
            if (author != null && !author.isBlank()) {
                spec = spec.and(PostSpecification.hasAuthor(author));
            }
            if (tag != null && !tag.isBlank()) {
                spec = spec.and(PostSpecification.hasTag(tag));
            }
            if (publishedDate != null && !publishedDate.isBlank()) {
                try {
                    LocalDate date = LocalDate.parse(publishedDate);
                    spec = spec.and(PostSpecification.hasPublishedDate(date));
                } catch (DateTimeParseException e) {
                    spec = spec.and(PostSpecification.hasPublishedDate(null));
                }
            }

            Sort sorting = Sort.by("publishedDate");
            if ("asc".equalsIgnoreCase(sort)) {
                sorting = sorting.ascending();
            } else {
                sorting = sorting.descending();
            }


            return postRepository.findAll(spec, sorting);
    }
//        LocalDate date = null;
//        if (publishedDate != null && !publishedDate.isBlank()) {
//            try {
//                date = LocalDate.parse(publishedDate);
//            } catch (DateTimeParseException e) {
//                // handle invalid date format or set date to null
//            }
//        }
//
//        if (search != null) {
//            search = search.trim();
//        }
//
//        // Pass null for any empty parameters to disable filters
//        return postRepository.searchPostsNative(
//                (search == null || search.isEmpty()) ? null : search,
//                (author == null || author.isEmpty()) ? null : author,
//                (tag == null || tag.isEmpty()) ? null : tag,
//                date,
//                (sort == null || (!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc"))) ? "desc" : sort.toLowerCase()
//        );


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
