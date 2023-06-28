package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        Article article = Article.builder()
                .deletedAt(LocalDate.now().atStartOfDay())
                .deleted(false)
                .build();

        testEntityManager.persist((article));
    }

    @Test
    @DisplayName("Create a new article")
    public void createArticle() {
        Article article = Article.builder()
                .deletedAt(LocalDate.now().atStartOfDay())
                .deleted(false)
                .build();

        articleRepository.save(article);
    }

    @Test
    @DisplayName("Return article based on Id")
    public void returnArticleWithId() {
        Optional<Article> article = articleRepository.findByIdAndDeletedFalse((0L));
        assertNotEquals(null, article );
        article.ifPresent(value -> assertEquals(0L, value.getId() ));
    }

    @Test
    @DisplayName("Find all with pagination")
    public void findWithPagination() {
        Pageable firstPage = PageRequest.of(0,3);
        Pageable secondPage = PageRequest.of(1,2);
        Pageable sortedPage = PageRequest.of(1,2, Sort.by("title"));
        List<Article> sorted = articleRepository.findAll(firstPage).getContent();
        articleRepository.findByDeletedFalse(sortedPage);
        Sort sort = Sort.by("title");
        List<Article> articles = articleRepository.findAll(sort);
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