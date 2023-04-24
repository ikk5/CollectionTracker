package com.tracker.collectiontracker.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "category_id" }) })
@Getter @Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Datatype datatype = Datatype.TEXT;

    @Column(name = "default_value")
    private String defaultValue;

    /**
     * True if the field should be hidden for other users.
     */
    @Column(nullable = false)
    private Boolean hidden = false;

    /**
     * True if it should be shown on the list view as a column.
     */
    @Column(nullable = false, name = "list_column")
    private Boolean listColumn = false;

    @Column(nullable = false, name = "display_order")
    private int displayOrder;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(orphanRemoval = true, mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Triplestore> triplestores = new ArrayList<>();

}
