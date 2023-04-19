package com.tracker.collectiontracker.to.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@AllArgsConstructor
@Getter @Setter
public class MessageResponse {
    private String message;

    /**
     * Id if an object was saved.
     */
    private Long id;

    public MessageResponse(String message) {
        this.message = message;
    }
}
