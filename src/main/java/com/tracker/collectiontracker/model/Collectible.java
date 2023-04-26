package com.tracker.collectiontracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

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
@ToString(exclude = { "images", "triples", "subcategory" })
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

    @OneToMany(orphanRemoval = true, mappedBy = "collectible", cascade = CascadeType.ALL)
    private final List<Triplestore> triples = new ArrayList<>();

    public void addImage(String url, int displayOrder) {
        if (StringUtils.isNotBlank(url)) {
            if (images == null) {
                images = new ArrayList<>();
            }
            images.add(new ImageLink(url, displayOrder, this));
        }
    }

    public List<ImageLink> getImages() {
        List<ImageLink> retval = new ArrayList<>();
        if (!CollectionUtils.isEmpty(images)) {
            retval = new ArrayList<>(images);
            retval.sort(Comparator.comparing(ImageLink::getDisplayOrder));
        }
        return retval;
    }

    public void clearImages() {
        images.forEach(img -> img.setCollectible(null));
        images.clear();
    }

    public void addOrUpdateTriple(String value, Question question) {
        Triplestore existing = null;
        for (Triplestore existingTriple : triples) {
            if (StringUtils.equals(existingTriple.getQuestion().getName(), question.getName())) {
                existing = existingTriple;
                break;
            }
        }
        if (existing == null) {
            Triplestore triple = Triplestore.builder().value(value).question(question).collectible(this).build();
            triples.add(triple);
        } else {
            existing.setValue(value);
        }
    }

    public List<Triplestore> getTriples() {
        return new ArrayList<>(triples);
    }
}
