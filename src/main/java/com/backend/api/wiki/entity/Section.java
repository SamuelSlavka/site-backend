package com.backend.api.wiki.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE Section SET deleted = TRUE WHERE id = ?")
@Table(name = "section")
@EqualsAndHashCode(exclude = "article")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Revision> revisions;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "revision_id", referencedColumnName = "id")
    private Revision latestRevision;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "supersection_id")
    private Set<Section> subsections = new HashSet<>();

    @Min(0)
    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder = 0;

    @Max(value = 3, message = "Depth too low")
    private Integer depth = 0;

    private Boolean deleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "id")
    private Article article;

    public Section() {
        this.deleted = Boolean.FALSE;
    }

}
