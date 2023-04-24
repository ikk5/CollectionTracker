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
public class QuestionTO {
    private Long id;

    private String question;

    private String datatype;

    private String defaultValue;

    private Boolean hidden;

    private Boolean listColumn;

    private Boolean filterColumn;

    private int displayOrder;
}
