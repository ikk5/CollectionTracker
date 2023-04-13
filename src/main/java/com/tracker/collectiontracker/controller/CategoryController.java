package com.tracker.collectiontracker.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
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

import com.tracker.collectiontracker.mapper.CategoryMapper;
import com.tracker.collectiontracker.mapper.QuestionMapper;
import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Datatype;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.repository.CategoryRepository;
import com.tracker.collectiontracker.repository.CollectibleRepository;
import com.tracker.collectiontracker.repository.QuestionRepository;
import com.tracker.collectiontracker.repository.SubcategoryRepository;
import com.tracker.collectiontracker.to.CategoryTO;
import com.tracker.collectiontracker.to.QuestionTO;
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
    private QuestionRepository questionRepository;

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
        ResponseEntity<CategoryTO> response = new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        try {
            Category category = CategoryMapper.mapTOtoEntity(categoryTO);

            if (isValidCategory(category)) {
                CategoryTO savedCategory = CategoryMapper.mapEntityToTO(categoryRepository.save(category));
                response = new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    private boolean isValidCategory(Category category) {
        return StringUtils.isNotBlank(category.getName());
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryTO> updateCategory(@PathVariable("id") long id, @RequestBody CategoryTO categoryTO) {
        ResponseEntity<CategoryTO> response;
        Optional<Category> categoryData = categoryRepository.findById(id);

        // TODO: valideer gereserveerde kolomnamen (Subcat, Name, id)

        if (StringUtils.isBlank(categoryTO.getName())) {
            response = new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else if (categoryData.isPresent()) {
            Category dbCategory = categoryData.get();
            dbCategory.setName(categoryTO.getName());

            updateSubcategories(categoryTO, dbCategory);
            updateQuestion(categoryTO, dbCategory);

            CategoryTO updatedCategory = CategoryMapper.mapEntityToTO(categoryRepository.save(dbCategory));
            response = new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return response;
    }

    private void updateSubcategories(CategoryTO categoryTO, Category dbCategory) {
        List<Subcategory> unusedSubcategories = dbCategory.getSubcategories();
        categoryTO.getSubcategories().removeIf(subcategoryTO -> StringUtils.isBlank(subcategoryTO.getSubcategory()));

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
    }

    private void updateQuestion(CategoryTO categoryTO, Category dbCategory) {
        List<Question> unusedQuestions = dbCategory.getQuestions();
        categoryTO.getQuestions().removeIf(questionTO -> StringUtils.isBlank(questionTO.getQuestion()));

        for (QuestionTO questionTO : categoryTO.getQuestions()) {
            if (questionTO.getId() == null) {
                // Subcategory is new, add to Category
                dbCategory.addQuestion(QuestionMapper.mapTOtoEntity(questionTO));
            } else {
                // Subcategory may be renamed, copy name just in case.
                Question dbQuestion =
                        dbCategory.getQuestions().stream().filter(question ->
                                        Objects.equals(question.getId(), questionTO.getId()))
                                .findFirst().orElse(null);
                if (dbQuestion != null) {
                    dbQuestion.setName(questionTO.getQuestion());
                    dbQuestion.setDatatype(Datatype.getByName(questionTO.getDatatype()));
                    dbQuestion.setDefaultValue(questionTO.getDefaultValue());
                    dbQuestion.setHidden(BooleanUtils.isTrue(questionTO.getHidden()));
                    dbQuestion.setListColumn(BooleanUtils.isTrue(questionTO.getListColumn()));
                    unusedQuestions.remove(dbQuestion);
                }
            }
        }

        if (!unusedQuestions.isEmpty()) {
            // These questions are deleted
            for (Question unused : unusedQuestions) {
                // TODO: delete triplestore
                dbCategory.deleteQuestion(unused);
                questionRepository.delete(unused);
            }
        }
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryTO> getCategoryById(@PathVariable("id") long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            CategoryTO to = CategoryMapper.mapEntityToTO(category.get());
            return new ResponseEntity<>(to, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") long id) {
        try {
            categoryRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/categories")
    public ResponseEntity<HttpStatus> deleteAllCategories() {
        try {
            categoryRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/datatypes")
    public ResponseEntity<List<String>> getAllDatatypes() {
        try {
            List<String> datatypes = Arrays.stream(Datatype.values()).map(Datatype::getName).toList();
            return new ResponseEntity<>(datatypes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
