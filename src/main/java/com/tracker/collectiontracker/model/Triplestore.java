package com.tracker.collectiontracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Collectible data stored as a triple, composed of collectible (subject) - question (predicate) - value (object).
 * For example the game Psychonauts (collectible) has regioncode (question) PAL (value).
 */
@Entity
@Table
@Getter @Setter
@ToString(exclude = { "collectible", "question" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Triplestore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "collectible_id", nullable = false)
    private Collectible collectible;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column
    private String value;
}
