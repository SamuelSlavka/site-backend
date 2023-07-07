package com.backend.api.wiki.service;

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
import java.util.Set;

@Service
public class SectionServiceImpl implements SectionService {

    private final ArticleRepository articleRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    public SectionServiceImpl(ArticleRepository articleRepository, SectionRepository sectionRepository) {
        this.articleRepository = articleRepository;
        this.sectionRepository = sectionRepository;
    }


    @Override
    public List<Section> getSections(Integer page) {
        Pageable sortedPage = PageRequest.of(page, 10, Sort.by("section_order"));
        return sectionRepository.findByDeletedFalse(sortedPage);
    }

    @Override
    public Section getSection(String id) throws NotFoundException {
        return sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
    }

    @Override
    public Section createSubSection(String id, String text) throws NotFoundException {
        Section superSection = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
        Revision revision = Revision.builder().text(text).createdAt(LocalDateTime.now()).deleted(false).build();
        Set<Section> sections = superSection.getSubsections();
        Section newSection = Section.builder().deleted(false).latestRevision(revision).revisions(List.of(revision)).depth(superSection.getDepth()+1).sectionOrder(sections.size()+1).build();
        sections.add(newSection);
        superSection.setSubsections(sections);
        sectionRepository.flush();
        return newSection;
    }

    @Override
    public Section createRevision(String id, String text) throws NotFoundException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
        Revision revision = Revision.builder().text(text).createdAt(LocalDateTime.now()).deleted(false).build();
        List<Revision> revisions = section.getRevisions();
        revisions.add(revision);
        section.setRevisions(revisions);
        section.setLatestRevision(revision);
        sectionRepository.flush();
        return section;
    }

    @Override
    public void deleteSection(String id) throws NotFoundException {
        sectionRepository.deleteById(id);
    }

    @Override
    public Section restoreSection(String id) throws NotFoundException {
        Section section = sectionRepository.findByIdAndDeletedTrue(id).orElseThrow(() -> new NotFoundException("Article not found"));
        section.setDeleted(false);
        return sectionRepository.save(section);
    }
}
