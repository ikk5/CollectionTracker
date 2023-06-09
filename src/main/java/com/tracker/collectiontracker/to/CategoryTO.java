package com.tracker.collectiontracker.to;

import java.util.List;

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
public class CategoryTO {

    private Long id;

    private String name;

    private List<SubcategoryTO> subcategories;

    private List<QuestionTO> questions;
}
