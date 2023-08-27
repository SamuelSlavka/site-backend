package com.backend.api.wiki.entity;

import com.backend.api.core.entity.OwnedEntity;
import com.backend.api.core.entity.SoftDeletableEntity;
import com.backend.api.wiki.model.RevisionCreationDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
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

    @NotBlank(message = "Missing revision Title")
    private String title;

    public Revision(RevisionCreationDto revisionContent) {
        this.text = revisionContent.getText();
        this.title = revisionContent.getTitle();
    }

    public Revision(RevisionCreationDto revisionContent, String user) {
        this.text = revisionContent.getText();
        this.title = revisionContent.getTitle();
        this.create(user);
    }

    @Override
    public String toString() {
        return String.format("Revision %s, title %s, text %s", this.id, this.text, this.title);
    }
}
