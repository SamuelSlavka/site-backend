package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SectionServiceImpl implements  SectionService {

    private final ArticleRepository articleRepository;
    private final SectionRepository sectionRepository;
    @Autowired
    public SectionServiceImpl(ArticleRepository articleRepository, SectionRepository sectionRepository) {
        this.articleRepository = articleRepository;
        this.sectionRepository = sectionRepository;
    }


    @Override
    public List<Section> getSections(Integer page) {
        Pageable sortedPage = PageRequest.of(page,10, Sort.by("section_order"));
        return sectionRepository.findByDeletedFalse(sortedPage);
    }

    @Override
    public Section getSection(Long id) throws NotFoundException {
        return sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
    }

    @Override
    public Section createSection(Long articleId, Revision revision) throws NotFoundException {
        Article article = articleRepository.findByIdAndDeletedFalse(articleId).orElseThrow(() -> new NotFoundException("Article not found"));
        List<Section> sections = article.getSections();
        return Section.builder().article(article).revisions(List.of(revision)).build();
    }

    @Override
    public Section deleteSection(Long id) throws NotFoundException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
        section.setDeleted(true);
        section.setDeletedAt(LocalDateTime.now());
        return sectionRepository.save(section);
    }

    @Override
    public Section restoreSection(Long id) throws NotFoundException {
        Section section = sectionRepository.findByIdAndDeletedTrue(id).orElseThrow(() -> new NotFoundException("Article not found"));
        section.setDeleted(false);
        section.setDeletedAt(null);
        return sectionRepository.save(section);
    }
}
