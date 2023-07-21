package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Section;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {
    Optional<Section> findByIdAndDeletedFalse(@NotNull String id);
}