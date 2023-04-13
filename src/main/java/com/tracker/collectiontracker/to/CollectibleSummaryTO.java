package com.tracker.collectiontracker.to;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
public class CollectibleSummaryTO {
    private Long id;

    private String name;

    private String subcategory;

    private LocalDate addedDate;

    private final Map<String, String> questionAnswers = new HashMap<>();

    public void addQuestionAnswer(String question, String anwer) {
        questionAnswers.put(question, anwer);
    }
}
