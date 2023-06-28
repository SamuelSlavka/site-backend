package com.backend.api.wiki.controller;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final Logger logger = LoggerFactory.getLogger((ArticleController.class));

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<Article> getArticles(@RequestParam Integer page) {
        return articleService.getArticles(page);
    }

    @GetMapping(path = "/deleted")
    public List<Article> getDeletedArticles() {
        return articleService.getDeletedArticles();
    }


    @PostMapping
    public void createArticle(@RequestBody String title) {
        articleService.createArticle(title);
    }

    @GetMapping(path = "/id/{articleId}")
    public Article getArticle(@PathVariable("articleId") Long id) throws NotFoundException {
        return articleService.getArticle(id);
    }

    @DeleteMapping(path = "/{articleId}")
    public void deleteArticle(@PathVariable("articleId") Long id) throws NotFoundException {
        articleService.deleteArticle(id);
    }

    @PostMapping(path = "/restore/{articleId}")
    public void restoreArticle(@PathVariable("articleId") Long id) throws NotFoundException {
        articleService.restoreArticle(id);
    }

    @PostMapping(path = "/addSection/{articleId}")
    public void addSectionArticle(@PathVariable("articleId") Long id, @RequestBody Revision revision) throws NotFoundException {
        articleService.addSecionArticle(id, revision);
    }
}
