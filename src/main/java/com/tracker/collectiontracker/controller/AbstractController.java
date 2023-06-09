package com.tracker.collectiontracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tracker.collectiontracker.model.User;
import com.tracker.collectiontracker.repository.UserRepository;
import com.tracker.collectiontracker.security.services.UserDetailsImpl;

/**
 *
 */
public abstract class AbstractController {

    public static final String ORIGINS = "${origins.url}";

    @Autowired
    protected UserRepository userRepository;

    protected User findLoggedInUser() throws UsernameNotFoundException {
        User user = null;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl userDetails) {
            user = findUserByUsername(userDetails.getUsername());
        }
        return user;
    }

    protected User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }

}
