package com.tracker.collectiontracker.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
@ToString(exclude = { "questions", "subcategories" })
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
        List<Subcategory> retval = new ArrayList<>();
        if (!CollectionUtils.isEmpty(subcategories)) {
            retval = new ArrayList<>(subcategories);
            retval.sort(Comparator.comparing(Subcategory::getDisplayOrder));
        }
        return retval;
    }

    public void addSubcategory(Long subcategoryId, String subcategoryName, int displayOrder) {
        if (subcategoryId == null) {
            addSubcategory(subcategoryName, displayOrder);
        } else {
            subcategories.add(Subcategory.builder().id(subcategoryId).name(subcategoryName).displayOrder(displayOrder).category(this).build());
        }
    }

    private void addSubcategory(String subcategoryName, int displayOrder) {
        subcategories.add(Subcategory.builder().name(subcategoryName).displayOrder(displayOrder).category(this).build());
    }

    public void addSubcategory(Subcategory subcategory) {
        subcategory.setCategory(this);
        subcategories.add(subcategory);
    }

    public void deleteSubcategory(Subcategory subcategory) {
        subcategories.remove(subcategory);
        subcategory.setCategory(null);
    }

    public List<Question> getQuestions() {
        List<Question> retval = new ArrayList<>();
        if (!CollectionUtils.isEmpty(questions)) {
            retval = new ArrayList<>(questions);
            retval.sort(Comparator.comparing(Question::getDisplayOrder));
        }
        return retval;
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
