package com.backend.api.wiki.entity;

import com.backend.api.core.entity.OwnedEntity;
import com.backend.api.core.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Where;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = SoftDeletableEntity.SOFT_DELETED_CLAUSE)
@Table(name = "revision")
public class Revision extends OwnedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Missing revision text")
    private String text;

    private String title;

    public Revision(String text, String title) {
        this.text = text;
        this.title = title;
    }
}
