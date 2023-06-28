package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.error.NotFoundException;

import java.util.List;


public interface ArticleService {
    public List<Article> getArticles(Integer page);

    public Article getArticle(Long id) throws NotFoundException;
    public Article getArticleByTitle(String title) throws NotFoundException;

    public List<Article> getDeletedArticles();

    public Article createArticle(String title);

    public Article deleteArticle(Long id) throws NotFoundException;

    public Article restoreArticle(Long id) throws NotFoundException;

    public Article addSecionArticle(long id, Revision revision) throws NotFoundException;
}
