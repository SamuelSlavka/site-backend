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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ArticleServiceTest {
    final String userId = "user-id";
    final String articleId = "article-id";
    Article article;
    Section section;
    ArticleListItemDto listItem;
    Pageable sortedPage;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private ArticleRepository articleRepository;
    @InjectMocks
    private ArticleServiceImpl articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        section = Section.builder().id("section-id").build();
        article = Article.builder().id(articleId).section(section).title("title").build();
        article.create(userId);
        listItem = article.getListItemDto();
        sortedPage = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    }

    @Test
    void getPublicArticles() {
        when(articleRepository.findPublicArticles(sortedPage)).thenReturn(List.of(article));
        List<ArticleListItemDto> articles = this.articleService.getPublicArticles(0);

        assertEquals(listItem, articles.get(0));
        verify(articleRepository, times(1)).findPublicArticles(sortedPage);
    }

    @Test
    void getUserArticles() {
        when(articleRepository.findPrivateArticles(userId, false, sortedPage)).thenReturn(List.of(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        List<ArticleListItemDto> articles = this.articleService.getUserArticles(0, userId);

        assertEquals(listItem, articles.get(0));
        verify(articleRepository, times(1)).findPrivateArticles(userId, false, sortedPage);
    }

    @Test
    void getAdminArticles() {
        when(articleRepository.findPrivateArticles(userId, true, sortedPage)).thenReturn(List.of(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);
        List<ArticleListItemDto> articles = this.articleService.getUserArticles(0, userId);

        assertEquals(listItem, articles.get(0));
        verify(articleRepository, times(1)).findPrivateArticles(userId, true, sortedPage);
    }

    @Test
    void createPublicArticle() {
        ArticleCreationDto art = new ArticleCreationDto("title", false);
        ArticleListItemDto createdArt = this.articleService.createArticle(art, userId);

        assertEquals(createdArt.getTitle(), art.getTitle());
        assertEquals(createdArt.getIsPrivate(), false);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void createPrivateArticle() {
        ArticleCreationDto art = new ArticleCreationDto("title", true);
        ArticleListItemDto createdArt = this.articleService.createArticle(art, userId);

        assertEquals(createdArt.getTitle(), art.getTitle());
        assertEquals(createdArt.getIsPrivate(), true);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void deleteArticle() throws ForbiddenException, NotFoundException {
        when(articleRepository.findByIdAndDeletedFalse(articleId)).thenReturn(Optional.ofNullable(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);

        this.articleService.deleteArticle(articleId, userId);

        assertTrue(article.isDeleted());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void deleteArticleAsAdmin() throws ForbiddenException, NotFoundException {
        when(articleRepository.findByIdAndDeletedFalse(articleId)).thenReturn(Optional.ofNullable(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);

        this.articleService.deleteArticle(articleId, "admin-id");

        assertTrue(article.isDeleted());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void deleteArticleAsNonOwner() {
        when(articleRepository.findByIdAndDeletedFalse(articleId)).thenReturn(Optional.ofNullable(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> this.articleService.deleteArticle(articleId, "other-id"));

        assertFalse(article.isDeleted());
        verify(articleRepository, times(0)).save(any(Article.class));
    }

    @Test
    void editArticle() throws ForbiddenException, NotFoundException {
        ArticleCreationDto art = new ArticleCreationDto("title2", false);

        when(articleRepository.findByIdAndDeletedFalse(articleId)).thenReturn(Optional.ofNullable(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);

        ArticleListItemDto created = this.articleService.editArticle(articleId, art, userId);

        assertEquals(created.getTitle(), art.getTitle());
        assertEquals(created.getIsPrivate(), art.getIsPrivate());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void editArticleAsAdmin() throws ForbiddenException, NotFoundException {
        ArticleCreationDto art = new ArticleCreationDto("title2", false);

        when(articleRepository.findByIdAndDeletedFalse(articleId)).thenReturn(Optional.ofNullable(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);

        ArticleListItemDto created = this.articleService.editArticle(articleId, art, "admin-id");

        assertEquals(created.getTitle(), art.getTitle());
        assertEquals(created.getIsPrivate(), art.getIsPrivate());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void editArticleAsOtherUser() {
        ArticleCreationDto art = new ArticleCreationDto("title2", false);

        when(articleRepository.findByIdAndDeletedFalse(articleId)).thenReturn(Optional.ofNullable(article));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> this.articleService.editArticle(articleId, art, "other-user-id"));
        verify(articleRepository, times(0)).save(any(Article.class));
    }
}