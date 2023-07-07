package com.backend.api.wiki.controller;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleDto;
import com.backend.api.wiki.model.ArticleListItemDto;
import com.backend.api.wiki.service.ArticleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/articles")
public class ArticleController {
    private final Logger logger = LoggerFactory.getLogger((ArticleController.class));
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<ArticleListItemDto> getArticles(@RequestParam Integer page, @AuthenticationPrincipal Jwt principal) {
        if (Objects.nonNull(principal)) {
            this.logger.error(principal.getSubject());
        }
        List<Article> articles = articleService.getArticles(page);
        return articles.stream().map(this::convertToListItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/deleted")
    public List<Article> getDeletedArticles() {
        return articleService.getDeletedArticles();
    }


    @PostMapping
    public ArticleListItemDto createArticle(@Valid @RequestBody ArticleCreationDto request, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();

        return convertToListItemDto(articleService.createArticle(request.getTitle(), userId));
    }

    @PutMapping(path = "/{articleId}")
    public void editArticle(@PathVariable("articleId") String id, @Valid @RequestBody ArticleCreationDto request) throws NotFoundException {
        articleService.editArticle(id, request);
    }

    @GetMapping(path = "/id/{articleId}")
    public ArticleDto getArticle(@PathVariable("articleId") String id) throws NotFoundException {
        return convertToDto(articleService.getArticle(id));
    }

    @DeleteMapping(path = "/{articleId}")
    public void deleteArticle(@PathVariable("articleId") String id) throws NotFoundException {
        articleService.deleteArticle(id);
    }

    @PostMapping(path = "/restore/{articleId}")
    public void restoreArticle(@PathVariable("articleId") String id) throws NotFoundException {
        articleService.restoreArticle(id);
    }

    private ArticleListItemDto convertToListItemDto(Article article) {
        ArticleListItemDto artItem = modelMapper.map(article, ArticleListItemDto.class);
        if (Objects.nonNull(article.getSection())) {
            artItem.setSuperSection(article.getSection().getId());
        }
        return artItem;
    }

    private ArticleDto convertToDto(Article article) {
        return modelMapper.map(article, ArticleDto.class);
    }
}
