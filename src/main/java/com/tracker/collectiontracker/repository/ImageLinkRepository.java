package com.tracker.collectiontracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracker.collectiontracker.model.ImageLink;

public interface ImageLinkRepository extends JpaRepository<ImageLink, Long> {
}
