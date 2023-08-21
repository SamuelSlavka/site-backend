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

    /**
     * Finds articles that are public
     *
     * @param pageable specified page from articles
     * @return return list of public articles
     */
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.categories WHERE a.isPrivate = FALSE ")
    List<Article> findPublicArticles(Pageable pageable);


    /**
     * Finds articles that are either public or created by current user or all articles if the user is admin
     *
     * @param userId   current user id
     * @param isAdmin  true if user is admin
     * @param pageable specified page from articles
     * @return return list of articles
     */
    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.categories WHERE (a.createdBy = ?1 OR a.isPrivate = FALSE OR " +
            "?2" + " = " + "TRUE)  ")
    List<Article> findPrivateArticles(@NotNull String userId, boolean isAdmin, Pageable pageable);

    Optional<Article> findByIdAndDeletedFalse(@NotNull String id);

}
