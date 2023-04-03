package com.tracker.collectiontracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class CollectionTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollectionTrackerApplication.class, args);
    }

}
