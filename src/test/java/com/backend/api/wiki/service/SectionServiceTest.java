package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.security.utils.KeycloakRoleConverter;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.model.SectionPaginationDto;
import com.backend.api.wiki.projection.SectionProjection;
import com.backend.api.wiki.projection.SectionProjectionImpl;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.SectionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    SectionPaginationDto page;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private HttpServletRequest servletRequest;

    @InjectMocks
    private SectionServiceImpl sectionService;

    @BeforeEach
    public void setUp() {
        page = new SectionPaginationDto(0, 10, 10, 0);
        MockitoAnnotations.openMocks(this);
        projection = new SectionProjectionImpl(sectionId, "rev", "super", 0, 0, userId, articleId, "text", "title");
        sectionDto = sectionService.sectionProjectionToDto(projection);
        article = Article.builder().id(articleId).isPrivate(false).isPubliclyEditable(false).build();
        article.create(userId);
        revisionCreation = new RevisionCreationDto("title", "text");
        section = Section.builder().depth(0).sectionOrder(0).subsections(new HashSet<>()).id(sectionId).article(article)
                .revisions(new ArrayList<>()).build();
        section.create(userId);
        privateArticle = Article.builder().id(privateArticleId).isPrivate(true).build();

        privateArticle.create(userId);
    }

    @Test
    @DisplayName("Get public section")
    void getPublicSection() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(article);

        List<SectionDto> sections = this.sectionService.getPublicSection(sectionId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }

    @Test
    @DisplayName("Get private section from public endpoint as admin")
    void getPublicSectionPrivateArticleAdmin() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);

        when(articleRepository.getReferenceById(articleId)).thenReturn(article);

        List<SectionDto> sections = this.sectionService.getPublicSection(sectionId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }


    @Test
    @DisplayName("Get unknown public section")
    void notFoundPublicSection() {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(article);
        assertThrows(NotFoundException.class, () -> this.sectionService.getPublicSection("other-id", page));
        verify(sectionRepository, times(1)).findRecursiveById("other-id", limit, 0, 0, 10);
    }

    @Test
    @DisplayName("Get unknown public section")
    void notAllowedPublicSection() {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArticle);
        assertThrows(ForbiddenException.class, () -> this.sectionService.getPublicSection(sectionId, page));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }

    @Test
    @DisplayName("Get private section as logged")
    void getPrivateSection() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArticle);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }

    @Test
    @DisplayName("Get public section as logged")
    void getPublicSectionLogged() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(articleRepository.getReferenceById(articleId)).thenReturn(article);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }

    @Test
    @DisplayName("Get public section as other logged")
    void getPublicSectionAsOtherUser() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));

        Article privateArt = Article.builder().id(privateArticleId).isPrivate(false).build();
        privateArt.create("other-user");

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }


    @Test
    @DisplayName("Get private section as other logged")
    void getPrivateSectionAsOtherUser() {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));

        Article privateArt = Article.builder().id(privateArticleId).isPrivate(true).build();
        privateArt.create("other-user");

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);
        assertThrows(ForbiddenException.class, () -> this.sectionService.getSection(sectionId, userId, page));

        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }


    @Test
    @DisplayName("Get private section as logged admin")
    void getPrivateSectionAsOtherAdmin() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);

        Article privateArt = Article.builder().id(privateArticleId).isPrivate(true).build();
        privateArt.create("other-user");

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }

    @Test
    @DisplayName("Get public section as logged admin")
    void getPublicSectionAsOtherAdmin() throws ForbiddenException, NotFoundException {
        when(sectionRepository.findRecursiveById(sectionId, limit, 0, 0, 10)).thenReturn(List.of(projection));
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);

        Article privateArt = Article.builder().id(privateArticleId).isPrivate(false).build();
        privateArt.create("other-user");

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        List<SectionDto> sections = this.sectionService.getSection(sectionId, userId, page);

        assertEquals(sectionDto, sections.get(0));
        verify(sectionRepository, times(1)).findRecursiveById(sectionId, limit, 0, 0, 10);
    }


    @Test
    @DisplayName("Create subsection as article creator")
    void createSubSection() throws ForbiddenException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).isPubliclyEditable(false).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        SectionDto res = this.sectionService.createSubSection(sectionId, revisionCreation, userId);

        assertEquals(revisionCreation.getTitle(), res.getTitle());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(1)).flush();
    }

    @Test
    @DisplayName("Create subsection as other")
    void createSubSectionAsOther() {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);

        Article privateArt = Article.builder().id(articleId).isPrivate(true).isPubliclyEditable(false).build();
        privateArt.create(userId);
        section.setArticle(privateArt);

        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));
        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        assertThrows(ForbiddenException.class, () -> this.sectionService.createSubSection(sectionId, revisionCreation
                , "other-user"));
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(0)).flush();
    }

    @Test
    @DisplayName("Create subsection as admin")
    void createSubSectionAsAdmin() throws ForbiddenException, NotFoundException {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(true);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).isPubliclyEditable(false).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);

        SectionDto res = this.sectionService.createSubSection(sectionId, revisionCreation, userId);

        assertEquals(revisionCreation.getTitle(), res.getTitle());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
        verify(sectionRepository, times(1)).flush();
    }


    @Test
    @DisplayName("Create revision as article creator")
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
    @DisplayName("Delete subsection as article creator")
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
    @DisplayName("Delete top level section")
    void deleteSectionTooDeep() {
        when(servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name())).thenReturn(false);
        when(sectionRepository.findByIdAndDeletedFalse(sectionId)).thenReturn(Optional.ofNullable(section));

        Article privateArt = Article.builder().id(articleId).isPrivate(true).build();
        privateArt.create(userId);

        when(articleRepository.getReferenceById(articleId)).thenReturn(privateArt);
        assertThrows(NotAllowedException.class, () -> this.sectionService.deleteSection(sectionId, userId));
        assertFalse(section.isDeleted());
        verify(sectionRepository, times(1)).findByIdAndDeletedFalse(sectionId);
    }
}