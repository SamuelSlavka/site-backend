package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.projection.SectionProjection;
import com.backend.api.wiki.projection.SectionProjectionImpl;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.SectionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SectionServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private HttpServletRequest servletRequest;

    @InjectMocks
    private SectionServiceImpl sectionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSections() {
    }

    @Test
    void getSection() throws ForbiddenException, NotFoundException {
        // Arrange
        String sectionId = "section-id";
        String revisionId = "rev-id";
        String articleId = "art-id";

        Revision revision = Revision.builder().title("title").text("text").build();
        Article article = Article.builder().id(articleId).isPrivate(false).build();
        Section section = Section.builder().article(article).id(sectionId).latestRevision(revision).build();
        section.setDeleted(false);
        SectionProjection proj = new SectionProjectionImpl(sectionId, revisionId, "super", 0,0, "creator", "art-id","text", "title");
        when(sectionRepository.findRecursiveById(sectionId, 10)).thenReturn(List.of(proj));
        when(articleRepository.getReferenceById(articleId)).thenReturn(article);
        // Act
        SectionDto sectionDto = new SectionDto("section-id", revisionId, "super",0,0,"creator", "art-id", "text", "title");

        List<SectionDto> result = sectionService.getSection(sectionId);

        // Assert
        assertEquals(sectionDto, result.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, 10);
    }

    @Test
    void createSection() {
    }

    @Test
    void deleteSection() {
    }

    @Test
    void restoreSection() {
    }
}