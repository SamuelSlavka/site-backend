package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.model.ArticleCreationDto;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.projection.SectionProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectionRepositoryTest {
    private final String sectionId = "section-id";
    private final String userId = "user-id";
    private Section section;
    private Section subSection;
    private RevisionCreationDto revCreation;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {

        revCreation = new RevisionCreationDto("text", "title");
        section = new Section(userId);
        Article article = new Article(new ArticleCreationDto("title", false), section, userId);
        testEntityManager.persist(article);


        subSection = new Section(new Revision(revCreation), section, userId);
        Section subSection1 = new Section(new Revision(revCreation), section, userId);
        testEntityManager.persist(subSection1);

        Section subSubSection = new Section(new Revision(revCreation), subSection, userId);
        Section subSubSection1 = new Section(new Revision(revCreation), subSection, userId);
        testEntityManager.persist(subSubSection);
        testEntityManager.persist(subSubSection1);

        subSection.setSubsections(Set.of(subSubSection, subSubSection1));
        section.setSubsections(Set.of(subSection, subSection1));
        section.setArticle(article);
        testEntityManager.persist(subSection);
        testEntityManager.persist(section);
    }

    @Test
    @DisplayName("Find public section")
    void findRecursiveById() {
        List<SectionProjection> sections = sectionRepository.findRecursiveById(section.getId(), 10);
        assertEquals(5, sections.size());
    }

    @Test
    @DisplayName("Find public section to depth 1")
    void findRecursiveByIdToDepth() {
        List<SectionProjection> sections = sectionRepository.findRecursiveById(section.getId(), 2);
        assertEquals(3, sections.size());
    }


    @Test
    @DisplayName("Find public section and skip deleted")
    void findRecursiveSkipDeleted() {
        subSection.delete();
        List<SectionProjection> sections = sectionRepository.findRecursiveById(section.getId(), 10);
        assertEquals(2, sections.size());
    }

    @Test
    @DisplayName("Find single section")
    void findByIdAndDeletedFalse() {
        Optional<Section> res = sectionRepository.findByIdAndDeletedFalse(section.getId());
        res.ifPresent(value -> assertEquals(section, value));
    }

}