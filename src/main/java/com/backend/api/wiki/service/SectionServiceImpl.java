package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.utils.KeycloakRoleConverter;
import com.backend.api.wiki.entity.Article;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.SectionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class SectionServiceImpl implements SectionService {

    private final ArticleRepository articleRepository;
    private final SectionRepository sectionRepository;

    @Autowired
    ConversionService conversionService;

    @Autowired
    public HttpServletRequest servletRequest;

    @Autowired
    public SectionServiceImpl(ArticleRepository articleRepository, SectionRepository sectionRepository) {
        this.articleRepository = articleRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public SectionDto getSection(String id) throws NotFoundException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));

        if(this.isAllowed(section)) {
            throw new ForbiddenException("You cant view this section");
        }
        return conversionService.convert(section, SectionDto.class);
    }



    @Override
    public SectionDto getSection(String sectionId, String userId) throws NotFoundException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(sectionId).orElseThrow(() -> new NotFoundException("Section not found"));

        if(this.isAllowed(section, userId)) {
            throw new ForbiddenException("You cant view this section");
        }
        return conversionService.convert(section, SectionDto.class);

    }

    @Override
    public Section createSubSection(String id, String text, String userId) throws NotFoundException, NotAllowedException, ForbiddenException {
        Section superSection = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));
        if(superSection.getDepth() == 3) {
            throw new NotAllowedException("Section depth is too low");
        }

        if(this.isAllowed(superSection, userId)) {
            throw new ForbiddenException("You cant view this section");
        }
        Revision revision = Revision.builder().text(text).createdAt(LocalDateTime.now()).deleted(false).build();

        Set<Section> sections = superSection.getSubsections();
        Section newSection = Section.builder().article(superSection.getArticle()).deleted(false).latestRevision(revision).revisions(List.of(revision)).depth(superSection.getDepth()+1).sectionOrder(sections.size()+1).build();
        sections.add(newSection);
        superSection.setSubsections(sections);
        sectionRepository.flush();
        return newSection;
    }

    @Override
    public Section createRevision(String id, String text, String userId) throws NotFoundException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));

        if(this.isAllowed(section, userId)) {
            throw new ForbiddenException("You cant view this section");
        }

        Revision revision = Revision.builder().text(text).createdAt(LocalDateTime.now()).deleted(false).build();
        List<Revision> revisions = section.getRevisions();
        revisions.add(revision);
        section.setRevisions(revisions);
        section.setLatestRevision(revision);
        sectionRepository.flush();
        return section;
    }

    @Override
    public void deleteSection(String id, String userId) throws NotFoundException, NotAllowedException, ForbiddenException {
        Section section = sectionRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException("Section not found"));

        if(this.isAllowed(section, userId)) {
            throw new ForbiddenException("You cant delete this section");
        }

        if(section.getDepth() == 0) {
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

    private boolean isAllowed(Section section) {
        Article article = section.getArticle();
        boolean isAdmin = this.servletRequest.isUserInRole(KeycloakRoleConverter.rolesEnum.ADMIN.name());
        return article.getIsPrivate() && !isAdmin;
    }
}
