package com.backend.api.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
public class OwnedEntity extends SoftDeletableEntity{
    @Column(name = "created_by")
    private String createdBy;

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
