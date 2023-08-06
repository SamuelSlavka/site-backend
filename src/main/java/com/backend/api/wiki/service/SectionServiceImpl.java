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
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.SectionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class SectionServiceImpl implements SectionService {

    private final Logger logger = LoggerFactory.getLogger((SectionServiceImpl.class));

    private final ArticleRepository articleRepository;

    private final SectionRepository sectionRepository;

    @Autowired
    public HttpServletRequest servletRequest;


    @Autowired
    public SectionServiceImpl(ArticleRepository articleRepository, SectionRepository sectionRepository) {
        this.articleRepository = articleRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    @Transactional
    public List<SectionDto> getSection(String id) throws NotFoundException, ForbiddenException {
        List<SectionProjection> sectionProjectionList = sectionRepository.findRecursiveById(id, 10);
        this.logger.info("Got " + sectionProjectionList.size() + " Sections");

        if (sectionProjectionList.isEmpty()) {
            throw new NotFoundException("Section not found");
        }

        List<SectionDto> sectionDtoList = sectionProjectionList.stream().map(this::SectionProjectionToDto).toList();
        SectionDto head = sectionDtoList.get(0);

        if (Objects.isNull(head.getArticle()) || this.isAllowed(head.getArticle())) {
            throw new ForbiddenException("You cant view this section");
        }

        return sectionDtoList;
    }


    @Override
    @Transactional
    public List<SectionDto> getSection(String sectionId, String userId) throws NotFoundException, ForbiddenException {
        this.logger.info("User {}", userId);
        List<SectionDto> sectionList = this.getSection(sectionId);
        SectionDto head = sectionList.get(0);

        if (this.isAllowed(head.getArticle(), userId)) {
            throw new ForbiddenException("You cant view this section");
        }
        return sectionList;
    }


    private SectionDto SectionProjectionToDto(SectionProjection proj) {
        return new SectionDto(proj.getId(),
                proj.getLatestRevision(),
                proj.getSuperSection(),
                proj.getSectionOrder(),
                proj.getDepth(),
                proj.getCreatedBy(),
                proj.getArticle(),
                proj.getText(),
                proj.getTitle()
        );
    }

    @Override
    @Transactional
    public SectionDto createSubSection(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, NotAllowedException, ForbiddenException {
        Section superSection = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
        this.logger.info("Started creating");
        if (superSection.getDepth() == 3) {
            throw new NotAllowedException("Section depth is too low");
        }

        if (this.isAllowed(superSection, userId)) {
            throw new ForbiddenException("You cant view this section");
        }
        Revision revision = new Revision(revisionContent.getText(), revisionContent.getTitle());
        this.logger.info("Rev");
        Set<Section> sections = superSection.getSubsections();
        Section newSection = new Section(revision, superSection);
        this.logger.info("Sec");
        sections.add(newSection);
        superSection.setSubsections(sections);
        this.logger.info("Sub");
        sectionRepository.save(newSection);
        sectionRepository.flush();
        return new SectionDto(newSection);
    }

    @Override
    @Transactional
    public SectionDto createRevision(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));

        if (this.isAllowed(section, userId)) {
            throw new ForbiddenException("You cant view this section");
        }

        Revision revision = Revision.builder().text(revisionContent.getText()).title(revisionContent.getTitle()).build();
        List<Revision> revisions = section.getRevisions();
        revisions.add(revision);
        section.setRevisions(revisions);
        section.setLatestRevision(revision);
        sectionRepository.flush();
        return new SectionDto(section);
    }

    @Override
    @Transactional
    public void deleteSection(String id, String userId) throws NotFoundException, NotAllowedException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));

        if (this.isAllowed(section, userId)) {
            throw new ForbiddenException("You cant delete this section");
        }

        if (section.getDepth() == 0) {
            throw new NotAllowedException("Cant delete top section");
        }
        section.setDeleted(true);
        sectionRepository.save(section);
    }


    private boolean isAllowed(Section section, String userId) {
        Article article = section.getArticle();
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return article.getIsPrivate() && !article.getCreatedBy().equals(userId) && !isAdmin;
    }

    private boolean isAllowed(String article_id, String userId) {
        Article article = this.articleRepository.getReferenceById(article_id);
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return article.getIsPrivate() && !article.getCreatedBy().equals(userId) && !isAdmin;
    }

    private boolean isAllowed(String article_id) {
        Article article = this.articleRepository.getReferenceById(article_id);
        boolean isAdmin = !Objects.isNull(this.servletRequest) && this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return article.getIsPrivate() && !isAdmin;
    }
}
