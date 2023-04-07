package com.tracker.collectiontracker.mapper;

import java.util.List;

import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.to.CollectibleTO;
import com.tracker.collectiontracker.to.ImageLinkTO;

/**
 *
 */
public class CollectibleMapper {

    private CollectibleMapper() {
    }

    public static List<CollectibleTO> mapEntityListToTOs(List<Collectible> collectibles) {
        return collectibles.stream().map(CollectibleMapper::mapEntityToTO).toList();
    }

    public static CollectibleTO mapEntityToTO(Collectible collectible) {
        return CollectibleTO.builder()
                .id(collectible.getId())
                .subcategory(SubcategoryMapper.mapEntityToTO(collectible.getSubcategory()))
                .images(collectible.getImages().stream().map(imageLink -> new ImageLinkTO(imageLink.getUrl())).toList())
                .name(collectible.getName())
                .addedDate(collectible.getAddedDate())
                .build();
    }

    public static Collectible mapTOtoEntity(CollectibleTO to, Subcategory subcategory) {
        Collectible collectible = Collectible.builder()
                .name(to.getName())
                .subcategory(subcategory)
                .build();
        to.getImages().forEach(img -> collectible.addImage(img.getUrl()));
        return collectible;
    }
}
