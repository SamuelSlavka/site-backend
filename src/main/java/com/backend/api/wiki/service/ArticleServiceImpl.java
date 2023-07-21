package com.backend.api.wiki.service;

import com.backend.api.utils.KeycloakRoleConverter;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Section;
import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.repository.ArticleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger((ArticleServiceImpl.class));
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Autowired
    public HttpServletRequest servletRequest;

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
        topSection.setArticle(localArticle);
        return articleRepository.save(localArticle);
    }

    @Override
    @Transactional
    public Article editArticle(String id, ArticleCreationDto data, String userId) throws NotFoundException, ForbiddenException {
        Article article = articleRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Article not found"));
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        if (Objects.nonNull(data.getTitle())) {
            article.setTitle(data.getTitle());
        }
        if (Objects.nonNull(data.getIsPrivate())) {
            article.setIsPrivate(data.getIsPrivate());
        }

        if (!userId.equals(article.getCreatedBy()) && !isAdmin) {
            throw  new ForbiddenException("Article edit not allowed");
        }
        articleRepository.save(article);
        return article;
    }


    @Override
    @Transactional
    public void deleteArticle(String articleId, String userId) throws NotFoundException, ForbiddenException {
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new NotFoundException("Article not found"));
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        if (!userId.equals(article.getCreatedBy()) && !isAdmin) {
            throw  new ForbiddenException("Article edit not allowed");
        }
        article.setDeleted(true);
        articleRepository.save(article);
    }
}
