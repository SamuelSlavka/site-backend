package com.backend.api.wiki.controller;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.model.ArticleCreationDto;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
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
                .isPrivate(false)
                .deleted(false)
                .build();
    }

    @Test
    @WithMockUser
    void createArticle() throws Exception {
        ArticleCreationDto request = new ArticleCreationDto("title", false);
        Mockito.when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(article);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"isPrivate\": false}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getArticle() throws Exception {
        Mockito.when(articleService.getArticle("some-uid")).thenReturn(article);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles/id/some-uid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((article.getTitle())));
    }

    @Test
    @WithMockUser
    void getArticles() throws Exception {
        Mockito.when(articleService.getPublicArticles(0)).thenReturn(List.of(article));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles?page=0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((article.getTitle())));
    }

    @Test
    void getDeletedArticles() {
    }


    @Test
    void deleteArticle() {
    }

    @Test
    void restoreArticle() {
    }

}