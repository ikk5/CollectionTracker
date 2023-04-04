package com.tracker.collectiontracker.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.to.CollectibleTO;

/**
 *
 */
public class CollectibleMapper {

    private CollectibleMapper() {
    }

    public static List<CollectibleTO> mapEntityListToTOs(List<Collectible> collectibles) {
        return collectibles.stream().map(CollectibleMapper::mapEntityToTO).collect(Collectors.toList());
    }

    public static CollectibleTO mapEntityToTO(Collectible collectible) {
        return CollectibleTO.builder()
                .id(collectible.getId())
                .subcategory(SubcategoryMapper.mapEntityToTO(collectible.getSubcategory()))
                .name(collectible.getName())
                .addedDate(collectible.getAddedDate())
                .build();
    }

    public static Collectible mapTOtoEntity(CollectibleTO to, Subcategory subcategory) {
        return Collectible.builder()
                .name(to.getName())
                .subcategory(subcategory)
                .build();
    }
}
