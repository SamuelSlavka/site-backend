package com.backend.api.wiki.repository;

import com.backend.api.core.repository.OwnedRepository;
import com.backend.api.wiki.entity.Article;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends OwnedRepository<Article, String> {

    List<Article> findByDeletedFalse(Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.categories WHERE a.isPrivate = FALSE ")
    List<Article> findPublicArticles(Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.section WHERE (a.createdBy = ?1 OR a.isPrivate = FALSE OR ?2 = TRUE)  ")
    List<Article> findArticles(@NotNull String userId, boolean isAdmin, Pageable pageable);

    List<Article> findAllByCreatedByOrIsPrivateFalse(@NotNull String userId, Pageable pageable);

    Optional<Article> findByIdAndDeletedFalse(@NotNull String id);

    List<Article> findByDeletedTrue();

    Optional<Article> findByTitleAndDeletedFalse(@NotNull String title);
}
