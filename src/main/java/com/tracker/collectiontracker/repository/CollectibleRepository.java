package com.tracker.collectiontracker.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Subcategory;

/**
 *
 */
public interface CollectibleRepository extends JpaRepository<Collectible, Long> {
    List<Collectible> findByNameContaining(String name);

    boolean existsBySubcategory(Subcategory subcategory);

    @EntityGraph(attributePaths = { "triples", "images", "subcategory" })
    Optional<Collectible> findById(long id);

    @EntityGraph(attributePaths = { "triples" })
    List<Collectible> findCollectiblesBySubcategoryIn(Collection<Subcategory> subcategories);
}
