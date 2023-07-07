package com.backend.api.wiki.controller;

import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.SectionCreationDto;
import com.backend.api.wiki.service.SectionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/sections")
public class SectionController {
    private final SectionService sectionService;
    private final Logger logger = LoggerFactory.getLogger((SectionController.class));

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public List<Section> getSections(@RequestParam Integer page) {
        return sectionService.getSections(page);
    }

    @PostMapping(path = "/id/{sectionId}")
    public Section createSubSection(@PathVariable("sectionId") String id, @Valid @RequestBody SectionCreationDto request) throws NotFoundException {
        return sectionService.createSubSection(id, request.getText());
    }

    @PutMapping(path = "/id/{sectionId}")
    public Section createRevision(@PathVariable("sectionId") String id, @Valid @RequestBody SectionCreationDto request) throws NotFoundException {
        return sectionService.createRevision(id, request.getText());
    }

    @DeleteMapping(path = "/id/{sectionId}")
    public Void deleteRevision(@PathVariable("sectionId") String id) throws NotFoundException {
        sectionService.deleteSection(id);
        return null;
    }

    @GetMapping(path = "/id/{sectionId}")
    public Section getSection(@PathVariable("sectionId") String id) throws NotFoundException {
        return sectionService.getSection(id);
    }
}
