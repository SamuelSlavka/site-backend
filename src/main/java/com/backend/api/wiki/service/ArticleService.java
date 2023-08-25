package com.backend.api.wiki.service;

import com.backend.api.core.model.PaginationDto;
import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;

import java.util.List;


/**
 * Service used for article manipulation
 */
public interface ArticleService {

    /**
     * Returns non deleted and public articles
     *
     * @param page starting page
     * @return returns list of articles represented as DTOs
     */
    List<ArticleListItemDto> getPublicArticles(PaginationDto page);

    /**
     * Returns list of articles that are either public or are visible for specified user
     *
     * @param page   starting page
     * @param userId id of logged user
     * @return returns list of articles represented as DTOs
     */
    List<ArticleListItemDto> getUserArticles(PaginationDto page, String userId);

    /**
     * Creates new article entity
     *
     * @param request object containing article title and isPrivate flag
     * @param userId  id of logged user
     * @return returns newly created object
     */
    ArticleListItemDto createArticle(ArticleCreationDto request, String userId);

    /**
     * Soft deletes article
     *
     * @param articleId id of article to be deleted
     * @param userId    id of logged user
     * @throws NotFoundException  thrown when article is not found
     * @throws ForbiddenException thrown when user doesn't have permission for this action
     */
    void deleteArticle(String articleId, String userId) throws NotFoundException, ForbiddenException;

    /**
     * Edits article entity
     *
     * @param id     id of article to be edited
     * @param data   object containing article title and isPrivate flag
     * @param userId id of logged user
     * @return returns edited article
     * @throws NotFoundException  thrown when article is not found
     * @throws ForbiddenException thrown when user doesn't have permission for this action
     */
    ArticleListItemDto editArticle(String id, ArticleCreationDto data, String userId) throws NotFoundException,
            ForbiddenException;
}
