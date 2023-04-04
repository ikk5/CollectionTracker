package com.tracker.collectiontracker.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    public List<Subcategory> getSubcategories() {
        return new ArrayList<>(subcategories);
    }

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private final List<Subcategory> subcategories = new ArrayList<>();

    public void addSubcategory(Long subcategoryId, String subcategoryName) {
        if (subcategoryId == null) {
            addSubcategory(subcategoryName);
        } else {
            subcategories.add(Subcategory.builder().id(subcategoryId).name(subcategoryName).category(this).build());
        }
    }

    private void addSubcategory(String subcategoryName) {
        subcategories.add(Subcategory.builder().name(subcategoryName).category(this).build());
    }

    public void deleteSubcategory(Subcategory subcategory) {
        subcategories.remove(subcategory);
    }
}
