package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.security.utils.KeycloakRoleConverter;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.RevisionCreationDto;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SectionServiceTest {
    final String articleId = "article-id";
    final String privateArticleId = "private-article-id";
    final String sectionId = "section-id";
    final String userId = "user-id";
    final Integer limit = 10;
    Section section;
    SectionDto sectionDto;
    SectionProjection projection;
    Article article;
    Article privateArticle;
    RevisionCreationDto revisionCreation;

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
        projection = new SectionProjectionImpl(sectionId, "rev", "super", 0, 0, userId, articleId, "text", "title");
        sectionDto = sectionService.sectionProjectionToDto(projection);
        article = Article.builder().id(articleId).isPrivate(false).build();
        article.create(userId);
        revisionCreation = new RevisionCreationDto("title", "text");
        Revision revision = new Revision(revisionCreation);
        section = Section.builder().depth(0).sectionOrder(0).subsections(new HashSet<>()).id(sectionId).article(article)
                .revisions(new ArrayList<>()).build();
        section.create(userId);
        privateArticle = Article.builder().id(privateArticleId).isPrivate(true).build();

        privateArticle.create(userId);
    }

    @Test
    void getPublicSection() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(article);

        List<SectionDto> sections = this.sectionService.getPublicSection(sectionId);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit);
    }

    @Test
    void notFoundPublicSection() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(article);
        assertThrows(NotFoundException.class, () -> {
            this.sectionService.getPublicSection("other-id");
        });
        verify(sectionRepository, times(1)).findRecursiveById("other-id", limit);
    }

    @Test
    void notAllowedPublicSection() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArticle);
        assertThrows(ForbiddenException.class, () -> {
            this.sectionService.getPublicSection(sectionId);
        });
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit);
    }

    @Test
    void getPrivateSection() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArticle);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit);
    }

    @Test
    void getPrivateSectionAsOtherUser() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit)).thenReturn(List.of(projection));

        Article privateArt = Article.builder().id(privateArticleId).isPrivate(true).build();
        privateArt.create("other-user");

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);
        assertThrows(ForbiddenException.class, () -> {
            this.sectionService.getSection(sectionId, userId);
        });

        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit);
    }

    @Test
    void getPrivateSectionAsOtherAdmin() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit)).thenReturn(List.of(projection));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);

        Article privateArt = Article.builder().id(privateArticleId).isPrivate(true).build();
        privateArt.create("other-user");

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit);
    }

    @Test
    void createSubSection() throws ForbiddenException, NotAllowedException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        SectionDto res = this.sectionService.createSubSection(sectionId, revisionCreation, userId);

        assertEquals(revisionCreation.getTitle(), res.getTitle());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(1)).flush();
    }

    @Test
    void createSubSectionAsOther() throws ForbiddenException, NotAllowedException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        assertThrows(ForbiddenException.class, () -> {
            this.sectionService.createSubSection(sectionId, revisionCreation, "other-user");
        });
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(0)).flush();
    }

    @Test
    void createSubSectionAsAdmin() throws ForbiddenException, NotAllowedException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        SectionDto res = this.sectionService.createSubSection(sectionId, revisionCreation, userId);

        assertEquals(revisionCreation.getTitle(), res.getTitle());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(1)).flush();
    }


    @Test
    void createRevision() throws ForbiddenException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        SectionDto res = this.sectionService.createRevision(sectionId, revisionCreation, userId);

        assertEquals(revisionCreation.getTitle(), res.getTitle());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(1)).flush();
    }

    @Test
    void deleteSection() throws ForbiddenException, NotAllowedException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);

        section.setDepth(1);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        this.sectionService.deleteSection(sectionId, userId);

        assertTrue(section.isDeleted());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
    }

    @Test
    void deleteSectionTooDeep() throws ForbiddenException, NotAllowedException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);
        assertThrows(NotAllowedException.class, () -> {
            this.sectionService.deleteSection(sectionId, userId);
        });
        assertFalse(section.isDeleted());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
    }
}