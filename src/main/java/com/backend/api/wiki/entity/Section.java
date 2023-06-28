package com.backend.api.wiki.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(name = "section")
public class Section {

    public Section() {
        this.deleted = false;
    }

    @Id
    @SequenceGenerator(name = "section_sequence", sequenceName = "section_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_sequence")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Revision> revisions;

    @OneToMany(mappedBy = "supersection")
    private List<Section> subsections;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supersection_id", nullable = true)
    private Section supersection;

    @Positive
    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder = 0;

    @Max(value = 3, message = "Depth too low")
    private Integer depth = 0;

    private Boolean deleted = Boolean.FALSE;

    @PastOrPresent
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id", nullable=false)
    @JsonIgnore
    private Article article;

}
