package com.tracker.collectiontracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.collectiontracker.mapper.CategoryMapper;
import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.repository.CategoryRepository;
import com.tracker.collectiontracker.repository.CollectibleRepository;
import com.tracker.collectiontracker.repository.SubcategoryRepository;
import com.tracker.collectiontracker.to.CategoryTO;
import com.tracker.collectiontracker.to.SubcategoryTO;

/**
 *
 */
@CrossOrigin(origins = { "http://localhost:8081" })
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private CollectibleRepository collectibleRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryTO>> getAllCategories(@RequestParam(required = false) String name) {
        try {
            List<Category> categories;
            if (name == null) {
                categories = new ArrayList<>(categoryRepository.findAll());
            } else {
                categories = List.of(categoryRepository.findByName(name));
            }

            if (categories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(CategoryMapper.mapEntityListToTOs(categories), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryTO> createCategory(@RequestBody CategoryTO categoryTO) {
        try {
            // TODO: validaties
            Category category = CategoryMapper.mapTOtoEntity(categoryTO);

            CategoryTO savedCategory = CategoryMapper.mapEntityToTO(categoryRepository.save(category));
            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryTO> updateCategory(@PathVariable("id") long id, @RequestBody CategoryTO categoryTO) {
        Optional<Category> categoryData = categoryRepository.findById(id);

        if (categoryData.isPresent()) {
            Category dbCategory = categoryData.get();
            dbCategory.setName(categoryTO.getCategory());

            List<Subcategory> unusedSubcategories = dbCategory.getSubcategories();
            for (SubcategoryTO subcategoryTO : categoryTO.getSubcategories()) {
                if (subcategoryTO.getSubcategoryId() == null) {
                    // Subcategory is new, add to Category
                    dbCategory.addSubcategory(null, subcategoryTO.getSubcategory());
                } else {
                    // Subcategory may be renamed, copy name just in case.
                    Subcategory dbSubcategory =
                            dbCategory.getSubcategories().stream().filter(subcategory ->
                                            Objects.equals(subcategory.getId(), subcategoryTO.getSubcategoryId()))
                                    .findFirst().orElse(null);
                    if (dbSubcategory != null) {
                        dbSubcategory.setName(subcategoryTO.getSubcategory());
                        unusedSubcategories.remove(dbSubcategory);
                    }
                }
            }

            if (!unusedSubcategories.isEmpty()) {
                // These subcategories are deleted, only delete if it's not used for a collectible.
                for (Subcategory unused : unusedSubcategories) {
                    if (collectibleRepository.findBySubcategory(unused).isEmpty()) {
                        dbCategory.deleteSubcategory(unused);
                        subcategoryRepository.delete(unused);
                    }
                }
            }

            CategoryTO updatedCategory = CategoryMapper.mapEntityToTO(categoryRepository.save(dbCategory));
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
