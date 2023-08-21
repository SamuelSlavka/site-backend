package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    private Article pubArticle;
    private Pageable page;

    @BeforeEach
    void setUp() {
        pubArticle = Article.builder().isPrivate(false).build();
        String userId = "user-id";
        pubArticle.create(userId);
        testEntityManager.persist((pubArticle));
        Article privArticle = Article.builder().isPrivate(true).build();
        privArticle.create(userId);
        testEntityManager.persist(privArticle);

        page = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Find public articles")
    public void findPublicArticles() {
        List<Article> articles = articleRepository.findPublicArticles(page);
        assertEquals(articles.size(), 1);
        assertEquals(articles.get(0), pubArticle);
    }

    @Test
    @DisplayName("Find public articles without deleted")
    public void findPublicArticlesWithoutDeleted() {
        pubArticle.delete();

        List<Article> articles = articleRepository.findPublicArticles(page);
        assertEquals(articles.size(), 0);
    }

    @Test
    @DisplayName("Return article based on Id")
    public void findByIdAndDeletedFalse() {
        Optional<Article> article = articleRepository.findByIdAndDeletedFalse((pubArticle.getId()));
        article.ifPresent(value -> assertEquals(pubArticle, value));
    }
}