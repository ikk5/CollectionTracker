package com.tracker.collectiontracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Collectible {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "subcategory_id", nullable = false)
    @ManyToOne
    private Subcategory subcategory;

    @Column(name = "date_added")
    private final LocalDate addedDate = LocalDate.now();

    @OneToMany(orphanRemoval = true, mappedBy = "collectible", cascade = CascadeType.ALL)
    private List<ImageLink> images = new ArrayList<>();

    public void addImage(String url) {
        if (StringUtils.isNotBlank(url)) {
            if (images == null) {
                images = new ArrayList<>();
            }
            images.add(new ImageLink(url, this));
        }
    }

    public void clearImages() {
        images.forEach(img -> img.setCollectible(null));
        images.clear();
    }
}
