package com.tracker.collectiontracker.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 */
@Entity
@Table
@Getter @Setter
@ToString
public class Collectible {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column(name = "date_added")
    private LocalDate addedDate = LocalDate.now();

    @Column
    private String description;

    @Column
    private boolean published;

    public Collectible() {

    }
    
    public Collectible(Collectible collectible){
        name =collectible.getName();
        description = collectible.getDescription();
    }
}
