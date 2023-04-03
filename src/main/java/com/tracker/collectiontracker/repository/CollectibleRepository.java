package com.tracker.collectiontracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Collectible;

/**
 *
 */
public interface CollectibleRepository extends JpaRepository<Collectible, Long> {
    List<Collectible> findByPublished(boolean published);

    List<Collectible> findByNameContaining(String name);
}
