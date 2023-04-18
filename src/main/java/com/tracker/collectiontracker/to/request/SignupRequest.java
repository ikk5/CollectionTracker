package com.tracker.collectiontracker.to.request;

import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter @Setter
public class SignupRequest {
    private String username;

    private String email;

    private String password;
}
