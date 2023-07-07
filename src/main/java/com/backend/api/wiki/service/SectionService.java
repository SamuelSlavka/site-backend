package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;

import java.util.List;

public interface SectionService {
    List<Section> getSections(Integer page);

    Section getSection(String id) throws NotFoundException;

    Section createSubSection(String id, String text) throws NotFoundException;
    Section createRevision(String id, String text) throws NotFoundException;

    void deleteSection(String id) throws NotFoundException;

    Section restoreSection(String id) throws NotFoundException;


}
