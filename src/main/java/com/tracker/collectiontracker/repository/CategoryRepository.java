package com.tracker.collectiontracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.User;

/**
 *
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoriesByUser(User user);
}
