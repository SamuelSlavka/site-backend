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

/**
 * Article controller that provides crud actions on Article entity
 */
@RestController
@RequestMapping(path = "/api/v1/articles")
public class ArticleController {
    private final Logger logger = LoggerFactory.getLogger((ArticleController.class));
    @Autowired
    private ArticleService articleService;

    /**
     * Get endpoint that fetches list of articles
     *
     * @param page      page offset from which the articles should come
     * @param principal autowired keycloak auth principal with user data
     * @return returns body of HTTP response with list of articles
     */
    @GetMapping
    public List<ArticleListItemDto> getArticles(@Valid @RequestParam Integer page,
                                                @AuthenticationPrincipal Jwt principal) {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            this.logger.info("User {} started fetched articles", userId);
            return articleService.getUserArticles(page, userId);

        } else {
            this.logger.error("Unknown user fetched articles");
            return articleService.getPublicArticles(page);
        }
    }

    /**
     * Post endpoint that creates an article
     *
     * @param request   object in HTTP request body containing article title and isPrivate flag
     * @param principal autowired keycloak auth principal with user data
     * @return returns newly created article
     */
    @PostMapping
    public ArticleListItemDto createArticle(@Valid @RequestBody ArticleCreationDto request,
                                            @AuthenticationPrincipal Jwt principal) {
        String userId = principal.getSubject();
        this.logger.info("User {} started creating article", userId);
        return articleService.createArticle(request, userId);
    }

    /**
     * Put endpoint for editing articles
     *
     * @param articleId uid of the article to be edited
     * @param request   obj in HTTP body containing article title and isPrivate flag
     * @param principal autowired keycloak auth principal with user data
     * @return returns newly created article
     * @throws NotFoundException  thrown if the article is not found
     * @throws ForbiddenException thrown if the user doesn't have sufficient rights
     */
    @RequestMapping(value = {"{articleId}"}, method = PUT)
    public ArticleListItemDto editArticle(@Valid @PathVariable("articleId") String articleId,
                                          @Valid @RequestBody ArticleCreationDto request,
                                          @AuthenticationPrincipal Jwt principal) throws NotFoundException,
            ForbiddenException {
        String userId = principal.getSubject();
        this.logger.info("User {} started editing article", userId);
        return articleService.editArticle(articleId, request, userId);
    }

    /**
     * Delete endpoint that soft deletes the specified article
     *
     * @param articleId the id of article that should be deleted
     * @param principal autowired keycloak auth principal with user data
     * @throws NotFoundException  thrown if the article is not found
     * @throws ForbiddenException thrown if the user doesn't have sufficient rights
     */
    @DeleteMapping(path = "/{articleId}")
    public void deleteArticle(@Valid @PathVariable("articleId") String articleId,
                              @AuthenticationPrincipal Jwt principal) throws NotFoundException, ForbiddenException {
        String userId = principal.getSubject();
        this.logger.info("User {} started deleting article", userId);
        articleService.deleteArticle(articleId, userId);
    }
}
