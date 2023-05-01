package com.tracker.collectiontracker.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.model.Triplestore;
import com.tracker.collectiontracker.to.CollectibleSummaryTO;
import com.tracker.collectiontracker.to.CollectibleTO;
import com.tracker.collectiontracker.to.CollectiblesListTO;
import com.tracker.collectiontracker.to.ImageLinkTO;
import com.tracker.collectiontracker.to.QuestionTO;
import com.tracker.collectiontracker.util.TimingUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
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
                .images(collectible.getImages() == null ? new ArrayList<>() :
                        collectible.getImages().stream().map(imageLink -> new ImageLinkTO(imageLink.getUrl(), imageLink.getDisplayOrder())).toList())
                .triples(TriplestoreMapper.mapEntityListToTOs(collectible.getTriples()))
                .name(collectible.getName())
                .addedDate(collectible.getAddedDate())
                .build();
    }

    public static Collectible mapTOtoEntity(CollectibleTO to, Subcategory subcategory) {
        Collectible collectible = Collectible.builder()
                .name(to.getName())
                .subcategory(subcategory)
                .build();
        to.getImages().forEach(img -> collectible.addImage(img.getUrl(), img.getDisplayOrder()));
        to.getTriples().forEach(tripleTO ->
                collectible.addOrUpdateTriple(tripleTO.getValue(), QuestionMapper.mapTOtoEntityWithId(tripleTO.getQuestion())));
        return collectible;
    }

    public static CollectiblesListTO mapEntitiesToCollectibleListTO(List<Collectible> collectibles, List<Question> questions) {
        long start = TimingUtil.start();
        List<QuestionTO> questionTOlist = QuestionMapper.mapEntityListToTOs(questions);
        log.info("Mapped {} questions in {}ms", questionTOlist.size(), TimingUtil.duration(start));
        start = TimingUtil.start();
        List<CollectibleSummaryTO> collectibleSummaries = collectibles.stream().map(CollectibleMapper::mapEntityToSummaryTO).toList();
        log.info("Mapped {} collectibles in {}ms", collectibleSummaries.size(), TimingUtil.duration(start));

        return CollectiblesListTO.builder()
                .questions(questionTOlist)
                .collectibleSummaries(collectibleSummaries)
                .build();
    }

    private static CollectibleSummaryTO mapEntityToSummaryTO(Collectible collectible) {
        CollectibleSummaryTO summaryTO = CollectibleSummaryTO.builder()
                .id(collectible.getId())
                .subcategory(collectible.getSubcategory().getName())
                .name(collectible.getName())
                .addedDate(collectible.getAddedDate())
                .build();
        for (Triplestore triple : collectible.getTriples()) {
            if (BooleanUtils.isTrue(triple.getQuestion().getListColumn())) {
                summaryTO.addQuestionAnswer(triple.getQuestion().getName(), triple.getValue());
            }
        }
        return summaryTO;
    }
}
