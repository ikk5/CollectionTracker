package com.tracker.collectiontracker.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.tracker.collectiontracker.to.CategoryTO;
import com.tracker.collectiontracker.to.QuestionTO;
import com.tracker.collectiontracker.to.SubcategoryTO;

/**
 * Testklasse voor {@link CategoryController}.
 */
class CategoryControllerTest {

    CategoryController categoryController = new CategoryController();

    @Test
    void testValidCategory() {
        try {
            Method method = categoryController.getClass().getDeclaredMethod("isValidCategory", CategoryTO.class);
            method.setAccessible(true);
            boolean valid = (boolean)method.invoke(categoryController, buildValidCategoryTO());
            assertTrue(valid);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    private CategoryTO buildValidCategoryTO() {
        List<QuestionTO> questions = new ArrayList<>();
        questions.add(QuestionTO.builder().question("Region").build());
        List<SubcategoryTO> subcategories = new ArrayList<>();
        subcategories.add(SubcategoryTO.builder().subcategory("Gamecube").build());
        return CategoryTO.builder()
                .name("name")
                .questions(questions)
                .subcategories(subcategories)
                .build();
    }

    @Test
    void testInvalidQuestion() {
        CategoryTO categoryTO = buildValidCategoryTO();
        categoryTO.getQuestions().get(0).setQuestion("Name"); // Name is a reserved column.

        try {
            Method method = categoryController.getClass().getDeclaredMethod("isValidCategory", CategoryTO.class);
            method.setAccessible(true);
            boolean valid = (boolean)method.invoke(categoryController, categoryTO);
            assertFalse(valid);
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}