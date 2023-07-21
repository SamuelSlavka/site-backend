package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.SectionDto;

public interface SectionService {
    SectionDto getSection(String id) throws NotFoundException, ForbiddenException;

    SectionDto getSection(String sectionId, String userId) throws NotFoundException, ForbiddenException;

    Section createSubSection(String id, String text, String userId) throws NotFoundException, NotAllowedException, ForbiddenException;

    Section createRevision(String id, String text, String userId) throws NotFoundException, ForbiddenException;

    void deleteSection(String id, String userId) throws NotFoundException, NotAllowedException, ForbiddenException;
}
