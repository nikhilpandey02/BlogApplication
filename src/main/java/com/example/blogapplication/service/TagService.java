package com.example.blogapplication.service;

import com.example.blogapplication.model.Tag;
import com.example.blogapplication.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    TagRepository tagRepository;
    public List<String> getAllTags() {
       return tagRepository.findDistinctTagNames();


    }
}
