package com.tracker.collectiontracker.mapper;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.to.CategoryTO;

/**
 *
 */
public class CategoryMapper {
    private CategoryMapper() {
    }

    public static List<CategoryTO> mapEntityListToTOs(List<Category> categories) {
        return categories.stream().map(CategoryMapper::mapEntityToTO).sorted(Comparator.comparing(CategoryTO::getName)).toList();
    }

    public static CategoryTO mapEntityToTO(Category category) {
        return CategoryTO.builder()
                .id(category.getId())
                .name(category.getName())
                .subcategories(SubcategoryMapper.mapEntityListToTOs(category.getSubcategories()))
                .questions(QuestionMapper.mapEntityListToTOs(category.getQuestions()))
                .build();
    }

    public static Category mapTOtoEntity(CategoryTO to) {
        Category category = Category.builder()
                .name(to.getName())
                .build();

        if (to.getSubcategories() != null) {
            to.getSubcategories().stream().filter(s -> StringUtils.isNotBlank(s.getSubcategory()))
                    .forEach(subcategoryTO ->
                            category.addSubcategory(subcategoryTO.getSubcategoryId(), subcategoryTO.getSubcategory()));
        }

        if (to.getQuestions() != null) {
            List<Question> questions = to.getQuestions().stream()
                    .filter(s -> StringUtils.isNotBlank(s.getQuestion()))
                    .map(QuestionMapper::mapTOtoEntity)
                    .toList();
            questions.forEach(category::addQuestion);
        }
        return category;
    }
}
