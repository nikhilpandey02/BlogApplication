package com.example.blogapplication.service;

import com.example.blogapplication.model.Role;
import com.example.blogapplication.model.User;
import com.example.blogapplication.repositories.RoleRepository;
import com.example.blogapplication.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public boolean registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }

        Role authorRole = roleRepository.findByName("ROLE_AUTHOR");
        if (authorRole == null) {
            authorRole = new Role();
            authorRole.setName("ROLE_AUTHOR");
            roleRepository.save(authorRole);
        }

        user.setRoles(new HashSet<>());
        user.getRoles().add(authorRole);
//        user.setPassword("{noop}" + user.getPassword());

        userRepository.save(user);
        return true;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
