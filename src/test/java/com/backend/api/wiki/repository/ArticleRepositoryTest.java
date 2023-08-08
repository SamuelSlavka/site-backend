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
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        Article article = Article.builder()
                .id("some-uid-two")
                .build();
        article.setDeleted(false);
        testEntityManager.persist((article));
    }

    @Test
    @DisplayName("Create a new article")
    public void createArticle() {
        Article article = Article.builder()
                .build();
        article.setDeleted(false);
        articleRepository.save(article);
    }

    @Test
    @DisplayName("Return article based on Id")
    public void returnArticleWithId() {
        Optional<Article> article = articleRepository.findByIdAndDeletedFalse(("some-uid-two"));
        assertNotEquals(null, article);
        article.ifPresent(value -> assertEquals("some-uid-two", value.getId()));
    }

    @Test
    @DisplayName("Find all with pagination")
    public void findWithPagination() {
        Pageable firstPage = PageRequest.of(0, 3);
        Pageable secondPage = PageRequest.of(1, 2);
        Pageable sortedPage = PageRequest.of(1, 2, Sort.by("title"));
        List<Article> sorted = articleRepository.findAll(firstPage).getContent();
        Sort sort = Sort.by("title");
    }

    @Test
    void findByDeletedFalse() {
    }

    @Test
    void findAllByDeletedFalse() {
    }

    @Test
    void findByIdAndDeletedFalse() {
    }

    @Test
    void findByIdAndDeletedTrue() {
    }

    @Test
    void findByDeletedTrue() {
    }

    @Test
    void findByTitleAndDeletedFalse() {
    }
}