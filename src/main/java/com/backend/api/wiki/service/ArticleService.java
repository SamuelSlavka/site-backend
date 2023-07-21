package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;

import java.util.List;


public interface ArticleService {
    List<Article> getArticles(Integer page);

    List<Article> getPublicArticles(Integer page);

    List<Article> getUserArticles(Integer page, String userId);

    Article getArticle(String id) throws NotFoundException;

    Article getArticleByTitle(String title) throws NotFoundException;

    List<Article> getDeletedArticles();

    Article createArticle(ArticleCreationDto request, String userId);

    void deleteArticle(String articleId, String userId) throws NotFoundException, ForbiddenException;
    
    Article editArticle(String id, ArticleCreationDto data, String userId) throws NotFoundException, ForbiddenException;
}
