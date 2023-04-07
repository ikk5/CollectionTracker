package com.tracker.collectiontracker.to;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 */
@Getter @Setter
@ToString
@AllArgsConstructor
public class ImageLinkTO {
    private Long id;

    private String url;

    public ImageLinkTO(String url) {
        this.url = url;
    }
}
