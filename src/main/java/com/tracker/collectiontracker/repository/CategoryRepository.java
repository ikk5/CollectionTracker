package com.tracker.collectiontracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Category;

/**
 *
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}
