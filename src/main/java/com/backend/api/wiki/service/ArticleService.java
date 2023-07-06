package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;

import java.util.List;


public interface ArticleService {
    List<Article> getArticles(Integer page);

    Article getArticle(String id) throws NotFoundException;

    Article getArticleByTitle(String title) throws NotFoundException;

    List<Article> getDeletedArticles();

    Article createArticle(String title, String userId);

    void deleteArticle(String id) throws NotFoundException;

    Article restoreArticle(String id) throws NotFoundException;

    Article editArticle(String id, ArticleCreationDto data) throws NotFoundException;
}
