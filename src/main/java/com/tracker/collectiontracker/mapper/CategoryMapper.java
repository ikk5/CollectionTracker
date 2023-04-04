package com.tracker.collectiontracker.mapper;

import java.util.List;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.to.CategoryTO;

/**
 *
 */
public class CategoryMapper {
    private CategoryMapper() {
    }

    public static List<CategoryTO> mapEntityListToTOs(List<Category> categories) {
        return categories.stream().map(CategoryMapper::mapEntityToTO).toList();
    }

    public static CategoryTO mapEntityToTO(Category category) {
        return CategoryTO.builder()
                .id(category.getId())
                .category(category.getName())
                .subcategories(SubcategoryMapper.mapEntityListToTOs(category.getSubcategories()))
                .build();
    }

    public static Category mapTOtoEntity(CategoryTO to) {
        Category category = Category.builder()
                .name(to.getCategory())
                .build();
        if (to.getSubcategories() != null) {
            to.getSubcategories().forEach(subcategoryTO ->
                    category.addSubcategory(subcategoryTO.getSubcategoryId(), subcategoryTO.getSubcategory()));
        }
        return category;
    }
}
