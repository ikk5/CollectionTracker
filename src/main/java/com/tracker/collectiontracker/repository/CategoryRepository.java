package com.tracker.collectiontracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.User;

/**
 *
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryByUser(User user);
}
