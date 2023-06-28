package com.backend.api.wiki.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
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
@Table(name = "revision")
public class Revision {
    public Revision() {
        this.deleted = Boolean.FALSE;
    }

    @Id
    @SequenceGenerator(name = "revision_sequence", sequenceName = "revision_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_sequence")
    private Long id;

    @NotBlank(message = "Missing revision title")
    @Length(max = 100, message = "Note revision too long")
    private String title;

    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Boolean deleted = Boolean.FALSE;

    @PastOrPresent
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Revision(String title, String text) {
        this.title = title;
        this.text = text;
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
    }
}
