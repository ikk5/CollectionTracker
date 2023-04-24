package com.tracker.collectiontracker.mapper;

import java.util.List;

import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.to.SubcategoryTO;

/**
 *
 */
public class SubcategoryMapper {
    private SubcategoryMapper() {

    }

    public static List<SubcategoryTO> mapEntityListToTOs(List<Subcategory> subcategories) {
        return subcategories.stream().map(SubcategoryMapper::mapEntityToTO).toList();
    }

    public static SubcategoryTO mapEntityToTO(Subcategory subcategory) {
        return SubcategoryTO.builder()
                .subcategoryId(subcategory.getId())
                .subcategory(subcategory.getName())
                .categoryId(subcategory.getCategory().getId())
                .category(subcategory.getCategory().getName())
                .username(subcategory.getCategory().getUser().getUsername())
                .collectibleCount(subcategory.getCollectibles().size())
                .displayOrder(subcategory.getDisplayOrder())
                .build();
    }
}
