package com.tracker.collectiontracker.mapper;

import java.util.ArrayList;
import java.util.List;

import com.tracker.collectiontracker.model.Triplestore;
import com.tracker.collectiontracker.to.TriplestoreTO;

/**
 *
 */
public class TriplestoreMapper {

    private TriplestoreMapper() {
    }

    public static List<TriplestoreTO> mapEntityListToTOs(List<Triplestore> triplestores) {
        List<TriplestoreTO> toList = new ArrayList<>();
        if (triplestores != null) {
            toList = triplestores.stream().map(TriplestoreMapper::mapEntityToTO).toList();
        }
        return toList;
    }

    public static TriplestoreTO mapEntityToTO(Triplestore triplestore) {
        return TriplestoreTO.builder()
                .id(triplestore.getId())
                .question(QuestionMapper.mapEntityToTO(triplestore.getQuestion()))
                .value(triplestore.getValue())
                .build();
    }
}
