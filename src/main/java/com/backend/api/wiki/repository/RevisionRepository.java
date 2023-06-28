package com.backend.api.wiki.repository;
import com.backend.api.wiki.entity.Revision;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {
    List<Revision> findByDeletedFalse();
    Optional<Revision> findByIdAndDeletedTrue(@NotNull Long id);
    List<Revision> findByDeletedTrue();
}
