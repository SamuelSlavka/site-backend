package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.repository.ArticleRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;
    @MockBean
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
        Article article = Article.builder()
                .id(0L)
                .deletedAt(LocalDate.now().atStartOfDay())
                .title("title")
                .deleted(false)
                .build();

        Mockito.when(articleRepository.findByTitleAndDeletedFalse(("title"))).thenReturn(Optional.ofNullable(article));
    }

    @Test
    @SneakyThrows
    @DisplayName("Return article based on Title")
    public void returnArticleWithValidName() {
        String articleTitle = "title";
        Article article = articleService.getArticleByTitle(articleTitle);

        assertEquals(article.getTitle(), articleTitle);
    }

    @Test()
    @DisplayName("Throw error on wrong article search")
    public void throwCorrectErrorOnTitleSearch()  {
        String articleTitle = "title2";
        Throwable exception = assertThrows(NotFoundException.class, () -> articleService.getArticleByTitle(articleTitle));

        assertEquals("Article not found", exception.getMessage());
    }
}