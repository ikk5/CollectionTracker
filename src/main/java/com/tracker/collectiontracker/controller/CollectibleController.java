package com.tracker.collectiontracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.collectiontracker.mapper.CollectibleMapper;
import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.repository.CollectibleRepository;
import com.tracker.collectiontracker.repository.SubcategoryRepository;
import com.tracker.collectiontracker.to.CollectibleTO;

/**
 *
 */
@CrossOrigin(origins = { "http://localhost:8081" })
@RestController
@RequestMapping("/api")
public class CollectibleController {

    @Autowired
    private CollectibleRepository collectibleRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @GetMapping("/collectibles")
    public ResponseEntity<List<CollectibleTO>> getAllCollectibles(@RequestParam(required = false) String name) {
        try {
            List<Collectible> collectibles;
            if (name == null) {
                collectibles = new ArrayList<>(collectibleRepository.findAll());
            } else {
                collectibles = new ArrayList<>(collectibleRepository.findByNameContaining(name));
            }

            if (collectibles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(CollectibleMapper.mapEntityListToTOs(collectibles), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/collectibles/{id}")
    public ResponseEntity<CollectibleTO> getCollectibleById(@PathVariable("id") long id) {
        Optional<Collectible> collectible = collectibleRepository.findById(id);
        if (collectible.isPresent()) {
            CollectibleTO to = CollectibleMapper.mapEntityToTO(collectible.get());
            return new ResponseEntity<>(to, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/collectibles")
    public ResponseEntity<CollectibleTO> createCollectible(@RequestBody CollectibleTO collectibleTO) {
        try {
            Subcategory subcategory = subcategoryRepository.findById(collectibleTO.getSubcategory().getSubcategoryId()).orElse(null);
            Collectible collectible = CollectibleMapper.mapTOtoEntity(collectibleTO, subcategory);
            if (isValidCollectible(collectible)) {
                CollectibleTO savedCollectible = CollectibleMapper.mapEntityToTO(collectibleRepository.save(collectible));
                return new ResponseEntity<>(savedCollectible, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidCollectible(Collectible collectible) {
        return StringUtils.isNotBlank(collectible.getName()) && collectible.getSubcategory() != null;
    }

    @PutMapping("/collectibles/{id}")
    public ResponseEntity<CollectibleTO> updateCollectible(@PathVariable("id") long id, @RequestBody CollectibleTO collectibleTO) {
        Optional<Collectible> collectibleData = collectibleRepository.findById(id);
        Subcategory subcategory = subcategoryRepository.findById(collectibleTO.getSubcategory().getSubcategoryId()).orElse(null);

        if (collectibleData.isPresent() && subcategory != null) {
            Collectible dbCollectible = collectibleData.get();
            dbCollectible.setName(collectibleTO.getName());
            dbCollectible.setSubcategory(subcategory);

            dbCollectible.clearImages();
            collectibleTO.getImages().forEach(img -> dbCollectible.addImage(img.getUrl()));

            if (isValidCollectible(dbCollectible)) {
                CollectibleTO updatedCollectable = CollectibleMapper.mapEntityToTO(collectibleRepository.save(dbCollectible));
                return new ResponseEntity<>(updatedCollectable, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/collectibles/{id}")
    public ResponseEntity<HttpStatus> deleteCollectible(@PathVariable("id") long id) {
        try {
            collectibleRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/collectibles")
    public ResponseEntity<HttpStatus> deleteAllCollectibles() {
        try {
            collectibleRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
