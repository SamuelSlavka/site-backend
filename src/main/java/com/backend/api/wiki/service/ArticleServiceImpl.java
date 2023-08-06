package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.security.utils.KeycloakRoleConverter;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;
import com.backend.api.wiki.repository.ArticleRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class ArticleServiceImpl implements ArticleService {
    private final Logger logger = LoggerFactory.getLogger((ArticleServiceImpl.class));
    private final ArticleRepository articleRepository;
    @Autowired
    public HttpServletRequest servletRequest;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    @Transactional
    public List<ArticleListItemDto> getPublicArticles(Integer page) {
        Pageable sortedPage = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        List<Article> articles = articleRepository.findPublicArticles(sortedPage);
        return articleToListDto(articles);
    }

    @Override
    @Transactional
    public List<ArticleListItemDto> getUserArticles(Integer page, String userId) {
        Pageable sortedPage = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        List<Article> articles = articleRepository.findAllByCreatedByOrIsPrivateFalse(userId, sortedPage);
        return articleToListDto(articles);
    }

    @Override
    @Transactional
    public ArticleListItemDto getArticle(String id) throws NotFoundException {
        return articleToDto(articleRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Article not found")));
    }

    @Override
    @Transactional
    public ArticleListItemDto getArticleByTitle(String title) throws NotFoundException {
        return articleToDto(articleRepository.findByTitleAndDeletedFalse(title).orElseThrow(() -> new NotFoundException("Article not found")));
    }

    @Override
    @Transactional
    public List<ArticleListItemDto> getDeletedArticles() {
        return articleToListDto(articleRepository.findByDeletedTrue());
    }

    @Override
    @Transactional
    public ArticleListItemDto createArticle(ArticleCreationDto request, String userId) {
        Section topSection = Section.builder().sectionOrder(0).depth(0).build();
        Article article = Article.builder().title(request.getTitle()).section(topSection).isPrivate(request.getIsPrivate()).build();
        topSection.setArticle(article);
        articleRepository.save(article);
        return articleToDto(article);
    }

    @Override
    @Transactional
    public ArticleListItemDto editArticle(String id, ArticleCreationDto data, String userId) throws NotFoundException, ForbiddenException {
        Article article = articleRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Article not found"));
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        if (Objects.nonNull(data.getTitle())) {
            article.setTitle(data.getTitle());
        }
        if (Objects.nonNull(data.getIsPrivate())) {
            article.setIsPrivate(data.getIsPrivate());
        }

        if (!userId.equals(article.getCreatedBy()) && !isAdmin) {
            throw new ForbiddenException("Article edit not allowed");
        }
        articleRepository.save(article);
        return articleToDto(article);
    }


    @Override
    @Transactional
    public void deleteArticle(String articleId, String userId) throws NotFoundException, ForbiddenException {
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new NotFoundException("Article not found"));
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        if (!userId.equals(article.getCreatedBy()) && !isAdmin) {
            throw new ForbiddenException("Article edit not allowed");
        }
        article.setDeleted(true);
        articleRepository.save(article);
    }

    private List<ArticleListItemDto> articleToListDto(List<Article> articles) {
        return articles.stream().map((this::articleToDto)).toList();
    }

    private ArticleListItemDto articleToDto(Article article) {
        return new ArticleListItemDto(article.getId(), article.getTitle(), article.getSection().getId(), article.getCreatedBy(), article.getIsPrivate(), article.getCategories());
    }
}
