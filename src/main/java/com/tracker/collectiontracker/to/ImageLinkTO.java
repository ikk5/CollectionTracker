package com.tracker.collectiontracker.to;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 */
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ImageLinkTO {
    private Long id;

    private String url;

    private Integer displayOrder;

    public ImageLinkTO(String url, int displayOrder) {
        this.url = url;
        this.displayOrder = displayOrder;
    }
}
