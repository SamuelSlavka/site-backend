package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.projection.SectionProjection;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest
class SectionRepositoryTest {

    private Section section;
    private Section subSection;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        RevisionCreationDto revCreation = new RevisionCreationDto("text", "title");
        String userId = "user-id";

        section = new Section(userId);

        Article article = new Article(new ArticleCreationDto("title", false, false), section, userId);
        articleRepository.save(article);
        articleRepository.flush();

        sectionRepository.save(section);
        sectionRepository.flush();
        subSection = new Section(new Revision(revCreation), section, userId);
        sectionRepository.save(subSection);
        sectionRepository.flush();
        Section subSection1 = new Section(new Revision(revCreation), section, userId);
        sectionRepository.save(subSection1);
        sectionRepository.flush();

        Section subSubSection = new Section(new Revision(revCreation), subSection, userId);
        sectionRepository.save(subSubSection);
        sectionRepository.flush();
        Section subSubSection1 = new Section(new Revision(revCreation), subSection, userId);
        sectionRepository.save(subSubSection1);
        sectionRepository.flush();

        subSection.setSubsections(Set.of(subSubSection, subSubSection1));
        section.setSubsections(Set.of(subSection, subSection1));
        section.setArticle(article);
        sectionRepository.flush();
    }

    @Test
    @DisplayName("Find public section")
    void findRecursiveById() {
        List<SectionProjection> sections = sectionRepository.findRecursiveById(section.getId(), 10, 0, 0, 10);
        assertEquals(5, sections.size());
    }

    @Test
    @DisplayName("Find public section to depth 1")
    void findRecursiveByIdToDepth() {
        List<SectionProjection> sections = sectionRepository.findRecursiveById(section.getId(), 2, 0, 0, 10);
        assertEquals(3, sections.size());
    }

    @Test
    @DisplayName("Find public section and skip deleted")
    void findRecursiveSkipDeleted() {
        subSection.delete();
        this.sectionRepository.save(subSection);
        this.sectionRepository.flush();
        List<SectionProjection> sections = sectionRepository.findRecursiveById(section.getId(), 10, 0, 0, 10);
        assertEquals(2, sections.size());
    }

    @Test
    @DisplayName("Find single section")
    void findByIdAndDeletedFalse() {
        Optional<Section> res = sectionRepository.findByIdAndDeletedFalse(section.getId());
        res.ifPresent(value -> assertEquals(section.getId(), value.getId()));
    }
}