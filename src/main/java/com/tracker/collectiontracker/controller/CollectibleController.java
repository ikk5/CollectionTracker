package com.tracker.collectiontracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.repository.CollectibleRepository;

/**
 *
 */
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class CollectibleController {

    @Autowired
    private CollectibleRepository collectibleRepository;

    @GetMapping("/collectibles")
    public ResponseEntity<List<Collectible>> getAllCollectibles(@RequestParam(required = false) String name) {
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
            return new ResponseEntity<>(collectibles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/collectibles/{id}")
    public ResponseEntity<Collectible> getCollectibleById(@PathVariable("id") long id) {
        Optional<Collectible> collectible = collectibleRepository.findById(id);

        return collectible.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/collectibles")
    public ResponseEntity<Collectible> createCollectible(@RequestBody Collectible collectible) {
        try{
            // TODO: validaties
            Collectible newCollectible = collectibleRepository.save(new Collectible(collectible));
            return new ResponseEntity<>(newCollectible, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/collectibles/{id}")
    public ResponseEntity<Collectible> updateCollectible(@PathVariable("id") long id, @RequestBody Collectible collectible) {
        Optional<Collectible> collectibleData = collectibleRepository.findById(id);

        if (collectibleData.isPresent()) {
            Collectible dbCollectible = collectibleData.get();
            dbCollectible.setName(collectible.getName());
            dbCollectible.setDescription(collectible.getDescription());
            dbCollectible.setPublished(collectible.isPublished());
            return new ResponseEntity<>(collectibleRepository.save(dbCollectible), HttpStatus.OK);
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

    @GetMapping("/collectibles/published")
    public ResponseEntity<List<Collectible>> findByPublished() {
        try {
            List<Collectible> collectibles = collectibleRepository.findByPublished(true);

            if (collectibles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(collectibles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
