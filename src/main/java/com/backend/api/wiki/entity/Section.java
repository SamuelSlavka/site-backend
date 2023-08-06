package com.backend.api.wiki.entity;

import com.backend.api.core.entity.OwnedEntity;
import com.backend.api.core.entity.SoftDeletableEntity;
import com.backend.api.wiki.model.SectionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"article", "superSection"}, callSuper = false)
@Where(clause = SoftDeletableEntity.SOFT_DELETED_CLAUSE)
@Table(name = "section")
public class Section extends OwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Revision> revisions;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "revision_id", referencedColumnName = "id")
    private Revision latestRevision;

    @OneToMany(mappedBy = "superSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Section> subsections = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "super_section_id")
    private Section superSection;

    @Min(0)
    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder = 0;

    @Max(value = 3, message = "Depth too low")
    private Integer depth = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    @JsonIgnore
    private Article article;

    public Section(Revision latestRevision, Section superSection) {
        this.revisions = List.of(latestRevision);
        this.latestRevision = latestRevision;
        this.superSection = superSection;
        this.article = superSection.getArticle();
        this.sectionOrder = 0;
        this.depth = superSection.getDepth() + 1;
    }

    public SectionDto getDto() {
        return new SectionDto(this);
    }
}
