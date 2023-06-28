package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Category;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDeletedFalse();
    Optional<Category> findByIdAndDeletedTrue(@NotNull Long id);
    List<Category> findByDeletedTrue();
}
