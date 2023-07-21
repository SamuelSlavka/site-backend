package com.backend.api.wiki.controller;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.SectionCreationDto;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.service.SectionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/api/v1/sections")
public class SectionController {
    private final SectionService sectionService;
    private final Logger logger = LoggerFactory.getLogger((SectionController.class));
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(path = "/id/{sectionId}")
    public SectionDto createSubSection(@PathVariable("sectionId") String sectionId, @Valid @RequestBody SectionCreationDto request, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            return convertToSectionDto(sectionService.createSubSection(sectionId, request.getText(), userId));
        }
        throw new NotAllowedException("You cant create new revision");
    }

    @PutMapping(path = "/id/{sectionId}")
    public SectionDto createRevision(@PathVariable("sectionId") String sectionId, @Valid @RequestBody SectionCreationDto request, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            return convertToSectionDto(sectionService.createRevision(sectionId, request.getText(), userId));
        }
        throw new NotAllowedException("You cant create new revision");
    }

    @DeleteMapping(path = "/id/{sectionId}")
    public Void deleteRevision(@PathVariable("sectionId") String sectionId, @AuthenticationPrincipal Jwt principal) throws NotFoundException, NotAllowedException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            sectionService.deleteSection(sectionId, userId);
            return null;
        }
        throw new NotAllowedException("You cant delete this section!");
    }

    @GetMapping(path = "/id/{sectionId}")
    public SectionDto getSection(@PathVariable("sectionId") String sectionId, @AuthenticationPrincipal Jwt principal) throws NotFoundException, ForbiddenException {
        if (Objects.nonNull(principal)) {
            String userId = principal.getSubject();
            return sectionService.getSection(sectionId, userId);

        } else {
            return sectionService.getSection(sectionId);
        }
    }

    private SectionDto convertToSectionDto(Section section) {
        SectionDto sectionDto = modelMapper.map(section, SectionDto.class);

        if (Objects.nonNull(section.getArticle())) {
            sectionDto.setCreatedBy(section.getArticle().getCreatedBy());
        }
        return sectionDto;
    }

}
