package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByDeletedFalse(Pageable pageable);

    List<Article> findAllByDeletedFalse();

    Optional<Article> findByIdAndDeletedFalse(@NotNull Long id);

    Optional<Article> findByIdAndDeletedTrue(@NotNull Long id);

    List<Article> findByDeletedTrue();

    Optional<Article> findByTitleAndDeletedFalse(@NotNull String title);
}
