package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.model.ArticleCreationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
    private Article pubArticle;
    private Pageable page;

    @BeforeEach
    void setUp() {
        articleRepository.deleteAll();
        pubArticle = new Article(new ArticleCreationDto("title", false, false), null, "user-id");
        articleRepository.save((pubArticle));

        articleRepository.flush();
        Article privArticle = new Article(new ArticleCreationDto("title", true, false), null, "user-id");
        articleRepository.save(privArticle);
        articleRepository.flush();

        page = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Find public articles")
    public void findPublicArticles() {
        List<Article> articles = articleRepository.findPublicArticles(page);
        assertEquals(articles.size(), 1);
        assertEquals(pubArticle.getTitle(), articles.get(0).getTitle());
        assertEquals(pubArticle.getIsPrivate(), articles.get(0).getIsPrivate());
        assertEquals(pubArticle.getCreatedBy(), articles.get(0).getCreatedBy());
        assertEquals(pubArticle.getId(), articles.get(0).getId());
    }

    @Test
    @DisplayName("Find public articles without deleted")
    public void findPublicArticlesWithoutDeleted() {
        pubArticle.delete();
        articleRepository.save(pubArticle);

        List<Article> articles = articleRepository.findPublicArticles(page);
        assertEquals(0, articles.size());
    }

    @Test
    @DisplayName("Return article based on Id")
    public void findByIdAndDeletedFalse() {
        Optional<Article> article = articleRepository.findByIdAndDeletedFalse((pubArticle.getId()));
        article.ifPresent(value -> assertEquals(pubArticle.getId(), value.getId()));
    }
}