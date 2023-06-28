package com.backend.api.wiki.controller;

import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.service.SectionService;
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

    @PostMapping
    public Section createSection(Long articleId, Revision revision) throws NotFoundException {
        return sectionService.createSection(articleId, revision);
    }

    @GetMapping(path = "/id/{sectionId}")
    public Section getSection(@PathVariable("sectionId") Long id) throws NotFoundException {
        return sectionService.getSection(id);
    }

    @DeleteMapping(path = "/{sectionId}")
    public void deleteSection(@PathVariable("sectionId") Long id) throws NotFoundException {
        sectionService.deleteSection(id);
    }

    @PostMapping(path = "/restore/{sectionId}")
    public void restoreSection(@PathVariable("sectionId") Long id) throws NotFoundException {
        sectionService.restoreSection(id);
    }
}
