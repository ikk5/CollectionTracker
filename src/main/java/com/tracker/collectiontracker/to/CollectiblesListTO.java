package com.tracker.collectiontracker.to;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Builder
@Getter @Setter
public class CollectiblesListTO {
    private List<CollectibleSummaryTO> collectibleSummaries;

    private List<QuestionTO> questions;
}
