package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getArticles(Integer page) {
        Pageable sortedPage = PageRequest.of(page,10, Sort.by("title"));
        return articleRepository.findByDeletedFalse(sortedPage);
    }

    @Override
    public Article getArticle(Long id) throws NotFoundException {
        return articleRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Article not found"));
    }

    @Override
    public Article getArticleByTitle(String title) throws NotFoundException {
        return articleRepository.findByTitleAndDeletedFalse(title).orElseThrow(() -> new NotFoundException("Article not found"));
    }

    @Override
    public List<Article> getDeletedArticles() {
        return articleRepository.findByDeletedTrue();
    }

    @Override
    public Article createArticle(String title) {
        Article localArticle = new Article();
        return articleRepository.save(localArticle);
    }

    @Override
    @Transactional
    public Article deleteArticle(Long id) throws NotFoundException {
        Article article = articleRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Article not found"));
        article.setDeleted(true);

        article.setDeletedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article restoreArticle(Long id) throws NotFoundException {
        Article article = articleRepository.findByIdAndDeletedTrue(id).orElseThrow(() -> new NotFoundException("Article not found"));
        article.setDeleted(false);
        article.setDeletedAt(null);
        return articleRepository.save(article);
    }

    @Override
    @Transactional
    public Article addSecionArticle(long id, Revision revision) throws NotFoundException {
        Article articleDB = articleRepository.findById(id).orElseThrow(() -> new NotFoundException("Article not found"));

        Revision newRevision = Revision.builder().title(revision.getTitle()).text(revision.getText()).build();
        Section newSection = Section.builder().article(articleDB).build();

        List<Section> sectionsDB = articleDB.getSections();

        newSection.setRevisions(List.of(newRevision));
        sectionsDB.add(newSection);
        articleDB.setSections(sectionsDB);

        return articleRepository.save(articleDB);
    }
}
