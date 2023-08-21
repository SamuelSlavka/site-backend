package com.backend.api.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@MappedSuperclass

@Data
public class SoftDeletableEntity {
    public static final String SOFT_DELETED_CLAUSE = "DELETED IS FALSE";

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;


    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
