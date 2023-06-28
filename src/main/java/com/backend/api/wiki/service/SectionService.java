package com.backend.api.wiki.service;

import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotFoundException;

import java.util.List;

public interface SectionService {
    public List<Section> getSections(Integer page);

    public Section getSection(Long id) throws NotFoundException;

    public Section createSection(Long articleId, Revision revision) throws NotFoundException;

    public Section deleteSection(Long id) throws NotFoundException;

    public Section restoreSection(Long id) throws NotFoundException;


}
