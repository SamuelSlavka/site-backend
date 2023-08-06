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

@RestController
@RequestMapping(path = "/api/v1/sections")
public class SectionController {
    private final Logger logger = LoggerFactory.getLogger((SectionController.class));
    @Autowired
    private SectionService sectionService;

    @PostMapping(path = "/id/{sectionId}")
    public SectionDto createSubSection(@PathVariable("sectionId") String sectionId, @Valid @RequestBody RevisionCreationDto request, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();

            SectionDto section = sectionService.createSubSection(sectionId, request, userId);
            this.logger.info("Subsection {} created by {}", section.getId(), userId);
            return section;
        }
        throw new NotAllowedException("You cant create new revision");
    }

    @PutMapping(path = "/id/{sectionId}")
    public SectionDto createRevision(@PathVariable("sectionId") String sectionId, @Valid @RequestBody RevisionCreationDto request, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            SectionDto section = sectionService.createRevision(sectionId, request, userId);
            this.logger.info("Subsection {} created by {}", section.getId(), userId);
            return section;
        }
        throw new NotAllowedException("You cant create new revision");
    }

    @DeleteMapping(path = "/id/{sectionId}")
    public Void deleteRevision(@PathVariable("sectionId") String sectionId, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            sectionService.deleteSection(sectionId, userId);
            this.logger.info("Subsection {} deleted by {}", sectionId, userId);
            return null;
        }
        throw new NotAllowedException("You cant delete this section!");
    }

    @GetMapping(path = "/id/{sectionId}")
    public List<SectionDto> getSection(@PathVariable("sectionId") String sectionId, @AuthenticationPrincipal Jwt principal) throws NotFoundException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            List<SectionDto> section = sectionService.getSection(sectionId, userId);
            this.logger.info("Section {} fetched by {}", sectionId, userId);
            return section;

        } else {
            return sectionService.getSection(sectionId);
        }
    }
}
