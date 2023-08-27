package com.backend.api.wiki.entity;

import com.backend.api.core.entity.SoftDeletableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = SoftDeletableEntity.SOFT_DELETED_CLAUSE)
@Table(name = "category", uniqueConstraints = @UniqueConstraint(name = "category_unique", columnNames =
        "category_name"))
public class Category extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category_description")
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Article> articles;
}
