package com.tracker.collectiontracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Subcategory;

/**
 *
 */
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findSubcategoriesByIdIn(List<Long> ids);
}
