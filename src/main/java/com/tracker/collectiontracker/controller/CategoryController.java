package com.tracker.collectiontracker.controller;

import static com.tracker.collectiontracker.controller.AbstractController.ORIGINS;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
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
import com.tracker.collectiontracker.model.User;
import com.tracker.collectiontracker.repository.CategoryRepository;
import com.tracker.collectiontracker.repository.CollectibleRepository;
import com.tracker.collectiontracker.repository.QuestionRepository;
import com.tracker.collectiontracker.repository.SubcategoryRepository;
import com.tracker.collectiontracker.to.CategoryTO;
import com.tracker.collectiontracker.to.QuestionTO;
import com.tracker.collectiontracker.to.SubcategoryTO;
import com.tracker.collectiontracker.to.response.MessageResponse;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@CrossOrigin(origins = ORIGINS, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class CategoryController extends AbstractController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CollectibleRepository collectibleRepository;

    private static final List<String> RESERVED_COLUMNAMES = List.of("name", "subcategory", "id");

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryTO>> getAllCategories(@RequestParam(required = false) String username) {
        try {
            List<Category> categories;
            User user;
            if (username == null) {
                user = findLoggedInUser();
            } else {
                user = findUserByUsername(username);
            }
            categories = categoryRepository.findCategoriesByUser(user);

            if (categories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(CategoryMapper.mapEntityListToTOs(categories), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<MessageResponse> createCategory(@RequestBody CategoryTO categoryTO) {
        ResponseEntity<MessageResponse> response;
        try {
            if (isValidCategory(categoryTO)) {
                User user = findLoggedInUser();
                Category category = CategoryMapper.mapTOtoEntity(categoryTO);
                user.addCategory(category);

                Category savedCategory = categoryRepository.save(category);
                response = new ResponseEntity<>(new MessageResponse("This category was saved successfully!", savedCategory.getId()), HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>(
                        new MessageResponse("This category is invalid, make sure the questions don't have a reserved name (id, name, subcategory)"),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            response = new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<MessageResponse> updateCategory(@PathVariable("id") long id, @RequestBody CategoryTO categoryTO) {
        ResponseEntity<MessageResponse> response;
        Optional<Category> categoryData = categoryRepository.findById(id);

        if (!isValidCategory(categoryTO)) {
            response = new ResponseEntity<>(
                    new MessageResponse("This category is invalid, make sure the questions don't have a reserved name (id, name, subcategory)"),
                    HttpStatus.BAD_REQUEST);
        } else if (categoryData.isPresent()) {
            Category dbCategory = categoryData.get();
            if (!isCategoryOwnedByLoggedInUser(dbCategory)) {
                log.warn("User {} tried to alter a different users' category (id {})", findLoggedInUser().getUsername(), id);
                return new ResponseEntity<>(new MessageResponse("You're not allowed to alter categories from other users."), HttpStatus.UNAUTHORIZED);
            }

            dbCategory.setName(categoryTO.getName());

            updateSubcategories(categoryTO, dbCategory);
            updateQuestion(categoryTO, dbCategory);
            categoryRepository.save(dbCategory);
            response = new ResponseEntity<>(new MessageResponse("Category successfully updated.", id), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new MessageResponse(String.format("No category found with id %s", id)), HttpStatus.NOT_FOUND);
        }
        return response;
    }

    private boolean isValidCategory(CategoryTO categoryTO) {
        boolean isValid = CollectionUtils.isEmpty(categoryTO.getQuestions()) ||
                categoryTO.getQuestions().stream().noneMatch(question ->
                        RESERVED_COLUMNAMES.contains(question.getQuestion().toLowerCase()));
        return isValid && StringUtils.isNotBlank(categoryTO.getName());
    }

    private void updateSubcategories(CategoryTO categoryTO, Category dbCategory) {
        List<Subcategory> unusedSubcategories = dbCategory.getSubcategories();
        categoryTO.getSubcategories().removeIf(subcategoryTO -> StringUtils.isBlank(subcategoryTO.getSubcategory()));

        for (SubcategoryTO subcategoryTO : categoryTO.getSubcategories()) {
            if (subcategoryTO.getSubcategoryId() == null) {
                // Subcategory is new, add to Category
                dbCategory.addSubcategory(null, subcategoryTO.getSubcategory(), subcategoryTO.getDisplayOrder());
            } else {
                // Subcategory may be renamed, copy name just in case.
                Subcategory dbSubcategory =
                        dbCategory.getSubcategories().stream().filter(subcategory ->
                                        Objects.equals(subcategory.getId(), subcategoryTO.getSubcategoryId()))
                                .findFirst().orElse(null);
                if (dbSubcategory != null) {
                    dbSubcategory.setName(subcategoryTO.getSubcategory());
                    dbSubcategory.setDisplayOrder(subcategoryTO.getDisplayOrder());
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
                    dbQuestion.setDisplayOrder(questionTO.getDisplayOrder());
                    unusedQuestions.remove(dbQuestion);
                }
            }
        }

        if (!unusedQuestions.isEmpty()) {
            // These questions are deleted
            for (Question unused : unusedQuestions) {
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
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable("id") long id) {
        ResponseEntity<MessageResponse> response;
        try {
            Category category = categoryRepository.findById(id).orElse(null);
            if (category == null) {
                log.warn("Attempt to delete category with id {}, which couldn't be found.", id);
                response = new ResponseEntity<>(new MessageResponse("Category not found."), HttpStatus.NO_CONTENT);
            } else if (isCategoryOwnedByLoggedInUser(category)) {
                categoryRepository.delete(category);
                response = new ResponseEntity<>(new MessageResponse("Category has been deleted."), HttpStatus.OK);
            } else {
                log.warn("User {} tried to delete a different users' category (id {})", findLoggedInUser().getUsername(), id);
                response = new ResponseEntity<>(new MessageResponse("You're not allowed to delete categories from other users."), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response = new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @DeleteMapping("/categories")
    public ResponseEntity<MessageResponse> deleteAllCategories() {
        try {
            User user = findLoggedInUser();
            user.clearCategories();
            categoryRepository.deleteAll(user.getCategories());
            return new ResponseEntity<>(new MessageResponse("All categories have been deleted."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
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

    private boolean isCategoryOwnedByLoggedInUser(Category category) {
        return StringUtils.equals(findLoggedInUser().getUsername(), category.getUser().getUsername());
    }
}
