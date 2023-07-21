package com.backend.api.wiki.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Builder
@Table(name = "revision")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE Section SET deleted = TRUE WHERE id = ?")
public class Revision {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Lob
    @NotBlank(message = "Missing revision content")
    private String text;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private Boolean deleted = Boolean.FALSE;

    public Revision() {
        this.deleted = Boolean.FALSE;
    }

    public Revision(String text) {
        this.text = text;
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
    }
}
