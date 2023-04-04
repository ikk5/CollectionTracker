package com.tracker.collectiontracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Subcategory;

/**
 *
 */
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    Subcategory findByNameAndCategory(String name, Category category);
}
