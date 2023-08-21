package com.backend.api.wiki.controller;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.service.SectionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Section controller that provides actions on sections and their revisions
 */
@RestController
@RequestMapping(path = "/api/v1/sections")
public class SectionController {
    private final Logger logger = LoggerFactory.getLogger((SectionController.class));
    @Autowired
    private SectionService sectionService;


    /**
     * Get endpoint that fetches list of all subsections of given section until desired depth
     *
     * @param sectionId uid of the top level section
     * @param principal autowired keycloak auth principal with user data
     * @return returns list of sections
     * @throws NotFoundException  thrown if the section is not found
     * @throws ForbiddenException thrown if the user doesn't have sufficient rights
     */
    @GetMapping(path = "/id/{sectionId}")
    public List<SectionDto> getSection(@PathVariable("sectionId") String sectionId,
                                       @AuthenticationPrincipal Jwt principal) throws NotFoundException,
            ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            List<SectionDto> section = sectionService.getSection(sectionId, userId);
            this.logger.info("Section {} fetched by {}", sectionId, userId);
            return section;

        } else {
            return sectionService.getPublicSection(sectionId);
        }
    }

    /**
     * Post endpoint that creates subsection under given section
     *
     * @param sectionId supersection id
     * @param request   request body obj that contains initial revision values
     * @param principal autowired keycloak auth principal with user data
     * @return returns newly created subsection
     * @throws NotFoundException   thrown if the article is not found
     * @throws NotAllowedException thrown if the action is not allowed (e.g. too deep)
     * @throws ForbiddenException  thrown if the user doesn't have sufficient rights
     */
    @PostMapping(path = "/id/{sectionId}")
    public SectionDto createSubSection(@Valid @PathVariable("sectionId") String sectionId,
                                       @Valid @RequestBody RevisionCreationDto request,
                                       @AuthenticationPrincipal Jwt principal) throws NotFoundException,
            NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();

            SectionDto section = sectionService.createSubSection(sectionId, request, userId);
            this.logger.info("Subsection {} created by {}", section.getId(), userId);
            return section;
        }
        throw new NotAllowedException("You cant create new revision");
    }

    /**
     * Put endpoint that creates new revision in given section
     *
     * @param sectionId selected section id
     * @param request   request body obj that contains initial revision values
     * @param principal autowired keycloak auth principal with user data
     * @return returns edited subsection
     * @throws NotFoundException   thrown if the article is not found
     * @throws NotAllowedException thrown if the the action is not allowed (e.g. too deep)
     * @throws ForbiddenException  thrown if the user doesn't have sufficient rights
     */
    @PutMapping(path = "/id/{sectionId}")
    public SectionDto createRevision(@PathVariable("sectionId") String sectionId,
                                     @Valid @RequestBody RevisionCreationDto request,
                                     @AuthenticationPrincipal Jwt principal) throws NotFoundException,
            NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            SectionDto section = sectionService.createRevision(sectionId, request, userId);
            this.logger.info("Revision {} created by {}", section.getId(), userId);
            return section;
        }
        throw new NotAllowedException("You cant create new revision");
    }

    /**
     * Delete endpoint that soft deletes given section
     *
     * @param sectionId selected section id
     * @param principal autowired keycloak auth principal with user data
     * @return returns void
     * @throws NotFoundException   thrown if the article is not found
     * @throws NotAllowedException thrown if the the action is not allowed (e.g. too deep)
     * @throws ForbiddenException  thrown if the user doesn't have sufficient rights
     */
    @DeleteMapping(path = "/id/{sectionId}")
    public Void deleteSection(@PathVariable("sectionId") String sectionId, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            sectionService.deleteSection(sectionId, userId);
            this.logger.info("Section {} deleted by {}", sectionId, userId);
            return null;
        }
        throw new NotAllowedException("You cant delete this section!");
    }
}
