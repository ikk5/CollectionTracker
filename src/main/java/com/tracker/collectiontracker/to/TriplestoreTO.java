package com.tracker.collectiontracker.to;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter @Setter
@Builder
public class TriplestoreTO {

    private Long id;

    private QuestionTO question;

    private String value;

}
