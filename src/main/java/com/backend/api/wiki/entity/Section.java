package com.backend.api.wiki.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE Section SET deleted = TRUE WHERE id = ?")
@Table(name = "section")
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
    @JoinColumn(name = "supersection_id", referencedColumnName = "id")
    private List<Section> subsections;

    @Min(0)
    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder = 0;

    @Max(value = 3, message = "Depth too low")
    private Integer depth = 0;

    private Boolean deleted = Boolean.FALSE;


    public Section() {
        this.deleted = false;
    }

}
