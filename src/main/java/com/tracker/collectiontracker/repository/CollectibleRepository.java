package com.tracker.collectiontracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Subcategory;

/**
 *
 */
public interface CollectibleRepository extends JpaRepository<Collectible, Long> {
    List<Collectible> findByNameContaining(String name);

    List<Collectible> findBySubcategory(Subcategory subcategory);
}
