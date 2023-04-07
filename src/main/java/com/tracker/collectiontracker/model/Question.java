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
 *
 */
@Entity
@Table
@Getter @Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Datatype datatype = Datatype.TEXT;

    @Column(name = "default_value")
    private String defaultValue;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    //    private final List<Subcategory> subcategories = new ArrayList<>();
}
