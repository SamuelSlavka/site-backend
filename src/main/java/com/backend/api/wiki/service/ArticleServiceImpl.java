package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getArticles(Integer page) {
        Pageable sortedPage = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return articleRepository.findByDeletedFalse(sortedPage);
    }

    @Override
    public List<Article> getPublicArticles(Integer page) {
        Pageable sortedPage = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return articleRepository.findByDeletedFalseAndIsPrivateFalse(sortedPage);
    }

    @Override
    public List<Article> getUserArticles(Integer page, String userId) {
        Pageable sortedPage = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        return articleRepository.findAllByCreatedByOrIsPrivateFalse(userId, sortedPage);
    }

    @Override
    public Article getArticle(String id) throws NotFoundException {
        return articleRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Article not found"));
    }

    @Override
    public Article getArticleByTitle(String title) throws NotFoundException {
        return articleRepository.findByTitleAndDeletedFalse(title).orElseThrow(() -> new NotFoundException("Article not found"));
    }

    @Override
    public List<Article> getDeletedArticles() {
        return articleRepository.findByDeletedTrue();
    }

    @Override
    public Article createArticle(ArticleCreationDto request, String userId) {
        Section topSection = Section.builder().sectionOrder(0).depth(0).deleted(false).build();
        Article localArticle = Article.builder().title(request.getTitle()).section(topSection).createdAt(LocalDateTime.now()).deleted(false).createdBy(userId).isPrivate(request.getIsPrivate()).build();

        return articleRepository.save(localArticle);
    }

    @Override
    public Article editArticle(String id, ArticleCreationDto data) throws NotFoundException {
        Article article = articleRepository.findByIdAndDeletedTrue(id).orElseThrow(() -> new NotFoundException("Article not found"));
        if (Objects.nonNull(data.getTitle())) {
            article.setTitle(data.getTitle());
        }
        if (Objects.nonNull(data.getIsPrivate())) {
            article.setIsPrivate(data.getIsPrivate());
        }
        articleRepository.save(article);
        return article;
    }


    @Override
    @Transactional
    public void deleteArticle(String id) throws NotFoundException {
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Article restoreArticle(String id) throws NotFoundException {
        Article article = articleRepository.findByIdAndDeletedTrue(id).orElseThrow(() -> new NotFoundException("Article not found"));
        article.setDeleted(false);
        articleRepository.save(article);
        return article;
    }
}
