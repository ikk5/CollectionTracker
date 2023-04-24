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
@ToString(exclude = "collectible")
@NoArgsConstructor
@AllArgsConstructor
public class ImageLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, name = "display_order")
    private int displayOrder;

    @JoinColumn(name = "collectible_id", nullable = false)
    @ManyToOne
    private Collectible collectible;

    public ImageLink(String url, int displayOrder, Collectible collectible) {
        this.url = url;
        this.displayOrder = displayOrder;
        this.collectible = collectible;
    }
}
