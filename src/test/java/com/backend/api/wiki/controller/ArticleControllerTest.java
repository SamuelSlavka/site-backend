package com.backend.api.wiki.controller;

import com.backend.api.utils.Utils;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import com.backend.api.wiki.repository.SectionRepository;
import com.backend.api.wiki.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @MockBean
    SecurityContext securityContext;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ArticleRepository articleRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private ArticleService articleService;
    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private Jwt jwt;
    private Article article;
    private ArticleListItemDto articleDto;


    @BeforeEach
    void setUp() {
        article = Article.builder()
                .id("some-uid")
                .title("title")
                .isPrivate(false)
                .build();

        articleDto = new ArticleListItemDto("some-uid","title", "Section", "created", false, List.of());
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getUserArticles() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.getUserArticles(0, "testSubject")).thenReturn(List.of(articleDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles?page=0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((article.getTitle())));
    }

    @Test
    void getPublicArticles() throws Exception {
        when(articleService.getPublicArticles(0)).thenReturn(List.of(articleDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles?page=0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((article.getTitle())));
    }

    @Test
    void createArticle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"isPrivate\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((article.getTitle())));
    }

    @Test
    void createArticleWithoutBody() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    void createArticleWithoutTitle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(post("/api/v1/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"isPrivate\": false}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    void editArticle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.editArticle(anyString(), any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(put("/api/v1/articles/some-uid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"isPrivate\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((article.getTitle())));
    }

    @Test
    void editArticleWithoutBody() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.editArticle(anyString(), any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(put("/api/v1/articles/some-uid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    void editArticleWithoutTitle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        when(articleService.editArticle(anyString(), any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(put("/api/v1/articles/some-uid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isPrivate\": false}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    void deleteArticle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", "testSubject"));
        mockMvc.perform(delete("/api/v1/articles/some-uid"))
                .andExpect(status().isOk());
    }
}