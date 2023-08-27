package com.backend.api.wiki.service;

import com.backend.api.core.model.PaginationDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @inheritDoc
 */
@Service
public class ArticleServiceImpl implements ArticleService {
    private final Logger logger = LoggerFactory.getLogger((ArticleServiceImpl.class));
    @Autowired
    private HttpServletRequest servletRequest;
    @Autowired
    private ArticleRepository articleRepository;

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public List<ArticleListItemDto> getPublicArticles(PaginationDto page) {
        Pageable sortedPage = PageRequest.of(page.getPage(), page.getPageSize(), Sort.by("createdAt").descending());
        List<Article> articles = articleRepository.findPublicArticles(sortedPage);
        return articleToListDto(articles);
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public List<ArticleListItemDto> getUserArticles(PaginationDto page, String userId) {
        Pageable sortedPage = PageRequest.of(page.getPage(), page.getPageSize(), Sort.by("createdAt").descending());
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        List<Article> articles = articleRepository.findPrivateArticles(userId, isAdmin, sortedPage);
        return articleToListDto(articles);
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public ArticleListItemDto createArticle(ArticleCreationDto request, String userId) {
        Section section = new Section(userId);
        Article article = new Article(request, section, userId);
        section.setArticle(article);

        articleRepository.save(article);
        this.logger.info("User {} created article {}", userId, article.getId());
        return article.getListItemDto();
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public ArticleListItemDto editArticle(String id, ArticleCreationDto data, String userId) throws NotFoundException
            , ForbiddenException {
        Article article = articleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Article not found"));
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        article.setTitle(data.getTitle());
        article.setIsPrivate(data.getIsPrivate());

        if (!userId.equals(article.getCreatedBy()) && !isAdmin) {
            throw new ForbiddenException("Article edit not allowed");
        }
        this.logger.info("User {} updated article {}", userId, article.getId());
        articleRepository.save(article);
        return article.getListItemDto();
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public void deleteArticle(String articleId, String userId) throws NotFoundException, ForbiddenException {
        Article article = articleRepository.findByIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new NotFoundException("Article not found"));
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());

        if (!userId.equals(article.getCreatedBy()) && !isAdmin) {
            throw new ForbiddenException("Article delete not allowed");
        }
        this.logger.info("User {} deleted article {}", userId, article.getId());
        article.delete();
        articleRepository.save(article);
    }

    /**
     * Transform article list to DTO list
     *
     * @param articles list of articles
     * @return list of DTOs
     */
    private List<ArticleListItemDto> articleToListDto(List<Article> articles) {
        return articles.stream().map(Article::getListItemDto).toList();
    }
}
