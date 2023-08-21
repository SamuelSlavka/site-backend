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

/**
 * @inheritDoc
 */
@Service
public class SectionServiceImpl implements SectionService {
    private final Logger logger = LoggerFactory.getLogger((SectionServiceImpl.class));
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private HttpServletRequest servletRequest;

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public List<SectionDto> getPublicSection(String sectionId) throws NotFoundException, ForbiddenException {
        List<SectionDto> sectionDtoList = getSectionList(sectionId, 10);
        SectionDto head = sectionDtoList.get(0);

        if (Objects.isNull(head.getArticle()) || this.cantReadPublic(head.getArticle())) {
            throw new ForbiddenException("You cant view this section");
        }

        this.logger.info("Got section {} and {} subsections", sectionId, sectionDtoList.size());
        return sectionDtoList;
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public List<SectionDto> getSection(String sectionId, String userId) throws NotFoundException, ForbiddenException {
        this.logger.info("User {}", userId);
        List<SectionDto> sectionList = this.getSectionList(sectionId, 10);
        SectionDto head = sectionList.get(0);

        if (this.cantReadAny(head.getArticle(), userId)) {
            throw new ForbiddenException("You cant view this section");
        }
        this.logger.info("User {} got section {} and {} subsections", userId, sectionId, sectionList.size());
        return sectionList;
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public SectionDto createSubSection(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, ForbiddenException {
        Section superSection = sectionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Section not found"));
        Set<Section> subSections = superSection.getSubsections();

        if (this.cantWrite(superSection, userId)) {
            throw new ForbiddenException("You cant view this section");
        }

        Revision revision = new Revision(revisionContent);
        revision.create(userId);
        Section newSection = new Section(revision, superSection, userId);
        newSection.create(userId);

        subSections.add(newSection);
        superSection.setSubsections(subSections);

        sectionRepository.save(newSection);
        sectionRepository.flush();
        this.logger.info("User {} created subsection {}", userId, newSection.getId());
        return new SectionDto(newSection);
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public SectionDto createRevision(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Section not found"));
        List<Revision> revisions = section.getRevisions();

        if (this.cantWrite(section, userId)) {
            throw new ForbiddenException("You cant view this section");
        }

        Revision revision = new Revision(revisionContent);

        revisions.add(revision);
        section.setRevisions(revisions);
        section.setLatestRevision(revision);
        sectionRepository.flush();
        this.logger.info("User {} updated revision {}", userId, revision.getId());
        return new SectionDto(section);
    }

    /**
     * @inheritDoc
     */
    @Override
    @Transactional
    public void deleteSection(String id, String userId) throws NotFoundException, NotAllowedException,
            ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Section not found"));

        if (this.cantWrite(section, userId)) {
            throw new ForbiddenException("You cant delete this section");
        }

        if (section.getDepth() == 0) {
            throw new NotAllowedException("Cant delete top section");
        }

        section.delete();
        sectionRepository.save(section);
        this.logger.info("User {} deleted section {}", userId, section.getId());
    }


    /**
     * Checks if the user has right for editing
     *
     * @param section section that user is accessing
     * @param userId  user id
     * @return returns true if user is allowed
     */
    private boolean cantWrite(Section section, String userId) {
        Article article = section.getArticle();
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return !article.getCreatedBy().equals(userId) && !isAdmin;
    }

    /**
     * Checks if the user has right for editing
     *
     * @param article_id article id containing this section
     * @param userId     user id
     * @return returns true if user is allowed
     */
    private boolean cantReadAny(String article_id, String userId) {
        Article article = this.articleRepository.getReferenceById(article_id);
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return !(article.getIsPrivate() && article.getCreatedBy().equals(userId)) && !isAdmin;
    }

    /**
     * Checks if the user has right for editing
     *
     * @param article_id article id containing this section
     * @return returns true if user is allowed
     */
    private boolean cantReadPublic(String article_id) {
        Article article = this.articleRepository.getReferenceById(article_id);
        boolean isAdmin =
                !Objects.isNull(this.servletRequest) && this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return article.getIsPrivate() && !isAdmin;
    }

    /**
     * Transform Section Projection to Dto
     *
     * @param proj input projection
     * @return returns section DTO
     */
    public SectionDto sectionProjectionToDto(SectionProjection proj) {
        return new SectionDto(proj.getId(), proj.getLatestRevision(), proj.getSuperSection(), proj.getSectionOrder(),
                proj.getDepth(), proj.getCreatedBy(), proj.getArticle(), proj.getText(), proj.getTitle());
    }


    /**
     * Fetches section list from repository and transforms it into Dto list
     *
     * @param sectionId parent section id
     * @param limit     depth limit
     * @return returns list of sections containing parent and descendants
     * @throws NotFoundException thrown when no section was found
     */
    private List<SectionDto> getSectionList(String sectionId, Integer limit) throws NotFoundException {
        List<SectionProjection> sectionProjectionList = sectionRepository.findRecursiveById(sectionId, limit);

        if (sectionProjectionList.isEmpty()) {
            throw new NotFoundException("Section not found");
        }

        return sectionProjectionList.stream().map(this::sectionProjectionToDto).toList();
    }

}
