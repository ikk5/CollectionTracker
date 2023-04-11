package com.tracker.collectiontracker.to;

import java.time.LocalDate;
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
public class CollectibleTO {

    private Long id;

    private String name;

    private SubcategoryTO subcategory;

    private List<ImageLinkTO> images;

    private List<TriplestoreTO> triples;

    private LocalDate addedDate;
}
