package com.backend.api.wiki.repository;

import com.backend.api.core.repository.OwnedRepository;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.projection.SectionProjection;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends OwnedRepository<Section, String> {

    @Query(nativeQuery = true, value = """
            WITH RECURSIVE section_recursive AS (
                SELECT
                    s.*,
                    r.text,
                    r.title
                FROM
                    section s
                    LEFT JOIN revision r ON r.id = s.revision_id
                WHERE
            		s.deleted IS FALSE AND
                    s.id = :id
                UNION
                ALL
                SELECT
                    child.*,
                    r.text,
                    r.title
                FROM
                    section child
                    LEFT JOIN revision r ON r.id = child.revision_id
                    INNER JOIN section_recursive sr ON child.super_section_id = sr.id
                WHERE
            		child.deleted IS FALSE AND
                    child.depth < :limit
            )
            SELECT
                sr.*
            FROM
                section_recursive sr
                ORDER BY sr.depth, sr.section_order
                        """)
    List<SectionProjection> findRecursiveById(String id, int limit);

    @Override
    @Nonnull
    List<Section> findAll();

    Optional<Section> findByIdAndDeletedFalse(@NotNull String id);
}
