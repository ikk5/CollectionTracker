package com.tracker.collectiontracker.to;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 */
@Builder
@ToString
@Getter @Setter
public class SubcategoryTO {

    private Long categoryId;

    private String category;

    private Long subcategoryId;

    private String subcategory;

    private String username;

    private int collectibleCount;

    private int displayOrder;
}
