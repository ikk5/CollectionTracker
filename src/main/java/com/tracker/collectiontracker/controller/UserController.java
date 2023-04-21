package com.tracker.collectiontracker.controller;

import static com.tracker.collectiontracker.controller.AbstractController.ORIGINS;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.collectiontracker.model.User;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@CrossOrigin(origins = ORIGINS, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class UserController extends AbstractController {

    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"));

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users.stream().map(User::getUsername).toList(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while fetching all users.", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
