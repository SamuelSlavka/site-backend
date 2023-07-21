package com.backend.api.wiki.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE Article SET deleted = TRUE WHERE id = ?")
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private Boolean deleted = Boolean.FALSE;

    @Column(name = "is_private")
    private Boolean isPrivate = Boolean.FALSE;

    @Column(name = "created_by")
    private String createdBy;

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "categories",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private Section section;

    public Article() {
        deleted = Boolean.FALSE;
    }

    public Article(String id, String title) {
        this.id = id;
        this.title = title;
        this.deleted = Boolean.FALSE;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", deleted=" + deleted +
                '}';
    }
}
