package com.backend.api.wiki.controller;

import com.backend.api.utils.Utils;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import com.backend.api.wiki.repository.SectionRepository;
import com.backend.api.wiki.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
    final String articleId = "some-uid";
    final String userId = "some-uid";
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
        article = Article.builder().id(articleId).title("title").isPrivate(false).build();

        articleDto = new ArticleListItemDto(articleId, "title", "section", userId, false, List.of());
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Return articles based on user Id")
    void getUserArticles() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.getUserArticles(0, userId)).thenReturn(List.of(articleDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles?page=0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value((article.getTitle())));
    }

    @Test
    @DisplayName("Return article based isPublic flag")
    void getPublicArticles() throws Exception {
        when(articleService.getPublicArticles(0)).thenReturn(List.of(articleDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/articles?page=0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value((article.getTitle())));
    }

    @Test
    @DisplayName("Create article")
    void createArticle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(post("/api/v1/articles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"isPrivate\": false}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((article.getTitle())));
    }

    @Test
    @DisplayName("Fail creating article without body")
    void createArticleWithoutBody() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(post("/api/v1/articles").contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    @DisplayName("Fail creating article")
    void createArticleWithoutTitle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.createArticle(any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(post("/api/v1/articles").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"isPrivate\": false}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    @DisplayName("Edit article")
    void editArticle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.editArticle(anyString(), any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(put("/api/v1/articles/" + articleId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"isPrivate\": false}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((article.getTitle())));
    }

    @Test
    @DisplayName("Fail editing article without body")
    void editArticleWithoutBody() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.editArticle(anyString(), any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(put("/api/v1/articles/" + articleId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    @DisplayName("Fail editing article")
    void editArticleWithoutTitle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(articleService.editArticle(anyString(), any(ArticleCreationDto.class), anyString())).thenReturn(articleDto);

        mockMvc.perform(put("/api/v1/articles/" + articleId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isPrivate\": false}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    @DisplayName("Delete article")
    void deleteArticle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        mockMvc.perform(delete("/api/v1/articles/" + articleId)).andExpect(status().isOk());
    }
}