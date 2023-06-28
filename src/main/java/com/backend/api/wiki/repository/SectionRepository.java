package com.backend.api.wiki.repository;
import com.backend.api.wiki.entity.Section;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByDeletedFalse(Pageable sortedPage);
    Optional<Section> findByIdAndDeletedFalse(@NotNull Long id);
    Optional<Section> findByIdAndDeletedTrue(@NotNull Long id);
}