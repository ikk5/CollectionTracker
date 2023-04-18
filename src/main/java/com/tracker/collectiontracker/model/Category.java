package com.tracker.collectiontracker.model;

import java.util.ArrayList;
import java.util.List;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "user_id" }) })
@Getter @Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private final List<Subcategory> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private final List<Question> questions = new ArrayList<>();

    public List<Subcategory> getSubcategories() {
        return new ArrayList<>(subcategories);
    }

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
        subcategory.setCategory(null);
    }

    public List<Question> getQuestions() {
        return new ArrayList<>(questions);
    }

    public void addQuestion(Question question) {
        question.setCategory(this);
        questions.add(question);
    }

    public void deleteQuestion(Question question) {
        questions.remove(question);
        question.setCategory(null);
    }
}
