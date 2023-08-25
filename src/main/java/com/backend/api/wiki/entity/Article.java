package com.backend.api.wiki.entity;

import com.backend.api.core.entity.OwnedEntity;
import com.backend.api.core.entity.SoftDeletableEntity;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.List;

/**
 * Article entity representing a set of sections either private or public
 */
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

    @Column(name = "is_public_editable")
    private Boolean isPubliclyEditable = Boolean.FALSE;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "categories", joinColumns = @JoinColumn(name = "article_id"), inverseJoinColumns =
    @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    @JsonIgnore
    private Section section;

    public Article(ArticleCreationDto request, Section section, String userId) {
        this.title = request.getTitle();
        this.section = section;
        this.isPrivate = request.getIsPrivate();
        this.isPubliclyEditable = request.getIsPubliclyEditable();
        this.create(userId);
    }

    public ArticleListItemDto getListItemDto() {
        return new ArticleListItemDto(this.getId(), this.getTitle(), this.getSection()
                .getId(), this.getCreatedBy(), this.getIsPrivate(), this.isPubliclyEditable, this.getCategories());
    }
}
