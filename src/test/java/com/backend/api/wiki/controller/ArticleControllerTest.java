package com.backend.api.wiki.controller;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import com.backend.api.wiki.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleRepository articleRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ArticleService articleService;

    private Article article;

    @BeforeEach
    void setUp() {
        article = Article.builder()
                .id("some-uid")
                .title("title")
                .deleted(false)
                .build();
    }

    @Test
    void createArticle() throws Exception {
        Mockito.when(articleService.createArticle("title", "user-id")).thenReturn(article);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"some-uid\", \"title\": \"title\",\"date\": \"2023-06-24\", \"deleted\": true}"))
                .andExpect(status().isOk());
    }

    @Test
    void getArticle() throws Exception {
        Mockito.when(articleService.getArticle("some-uid")).thenReturn(article);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles/id/some-uid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((article.getTitle())));
    }

    @Test
    void getArticles() throws Exception {
        Mockito.when(articleService.getArticles(0)).thenReturn(List.of(article));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles?page=0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((article.getTitle())));
    }

    @Test
    void getDeletedArticles() {
    }

    @Test
    void testCreateArticle() {
    }

    @Test
    void testGetArticle() {
    }

    @Test
    void deleteArticle() {
    }

    @Test
    void restoreArticle() {
    }

    @Test
    void addSectionArticle() {
    }
}