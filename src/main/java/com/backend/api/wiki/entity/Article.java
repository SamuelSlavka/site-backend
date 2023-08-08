package com.backend.api.wiki.entity;

import com.backend.api.core.entity.OwnedEntity;
import com.backend.api.core.entity.SoftDeletableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Where(clause = SoftDeletableEntity.SOFT_DELETED_CLAUSE)
@Table(name = "article")
public class Article extends OwnedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @Column(name = "is_private")
    private Boolean isPrivate = Boolean.FALSE;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "categories",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    @JsonIgnore
    private Section section;

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                '}';
    }
}
