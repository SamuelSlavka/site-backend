package com.backend.api.wiki.controller;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.ArticleListItemDto;
import com.backend.api.wiki.service.ArticleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/api/v1/articles")
public class ArticleController {
    private final Logger logger = LoggerFactory.getLogger((ArticleController.class));
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public List<ArticleListItemDto> getArticles(@Valid @RequestParam Integer page, @AuthenticationPrincipal Jwt principal) {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            this.logger.info("User {} started fetched articles", userId);
            return articleService.getUserArticles(page, userId);

        } else {
            this.logger.error("Unknown user fetched articles");
            return articleService.getPublicArticles(page);
        }
    }

    @PostMapping
    public ArticleListItemDto createArticle(@Valid @RequestBody ArticleCreationDto request, @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        this.logger.info("User {} started creating article", userId);
        return articleService.createArticle(request, userId);
    }

    @RequestMapping(value = {"{articleId}"}, method = PUT)
    public ArticleListItemDto editArticle(@Valid @PathVariable("articleId") String articleId, @Valid @RequestBody ArticleCreationDto request, @AuthenticationPrincipal Jwt principal) throws NotFoundException, ForbiddenException {
        String userId = principal.getSubject();
        this.logger.info("User {} started editing article", userId);
        return articleService.editArticle(articleId, request, userId);
    }

    @DeleteMapping(path = "/{articleId}")
    public void deleteArticle(@Valid @PathVariable("articleId") String articleId, @AuthenticationPrincipal Jwt principal) throws NotFoundException, ForbiddenException {
        String userId = principal.getSubject();
        this.logger.info("User {} started deleting article", userId);
        articleService.deleteArticle(articleId, userId);
    }
}
