package com.tracker.collectiontracker.mapper;

import java.util.Comparator;
import java.util.List;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.to.SubcategoryTO;

/**
 *
 */
public class SubcategoryMapper {
    private SubcategoryMapper() {

    }

    public static List<SubcategoryTO> mapEntityListToTOs(List<Subcategory> subcategories) {
        return subcategories.stream().map(SubcategoryMapper::mapEntityToTO).sorted(Comparator.comparing(SubcategoryTO::getSubcategory)).toList();
    }

    public static SubcategoryTO mapEntityToTO(Subcategory subcategory) {
        return SubcategoryTO.builder()
                .subcategoryId(subcategory.getId())
                .subcategory(subcategory.getName())
                .categoryId(subcategory.getCategory().getId())
                .category(subcategory.getCategory().getName())
                .build();
    }

    public static Subcategory mapTOtoEntity(SubcategoryTO to, Category category) {
        return Subcategory.builder()
                .category(category)
                .name(to.getSubcategory())
                .build();
    }
}
