package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {

    List<Article> findByDeletedFalse(Pageable pageable);


    Optional<Article> findByIdAndDeletedFalse(@NotNull String id);

    Optional<Article> findByIdAndDeletedTrue(@NotNull String id);

    List<Article> findByDeletedTrue();

    Optional<Article> findByTitleAndDeletedFalse(@NotNull String title);
}
