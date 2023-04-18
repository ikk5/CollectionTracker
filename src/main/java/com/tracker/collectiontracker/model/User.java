package com.tracker.collectiontracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@ToString(exclude = "categories")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "join_date")
    private LocalDate joinDate = LocalDate.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(orphanRemoval = true, mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Category> categories = new ArrayList<>();

    public void addCategory(Category category) {
        categories.add(category);
        category.setUser(this);
    }

    public void deleteCategory(Category category) {
        categories.remove(category);
        category.setUser(null);
    }
}
