package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;

import java.util.List;


public interface ArticleService {

    List<ArticleListItemDto> getPublicArticles(Integer page);

    List<ArticleListItemDto> getUserArticles(Integer page, String userId);

    ArticleListItemDto createArticle(ArticleCreationDto request, String userId);

    void deleteArticle(String articleId, String userId) throws NotFoundException, ForbiddenException;

    ArticleListItemDto editArticle(String id, ArticleCreationDto data, String userId) throws NotFoundException, ForbiddenException;
}
