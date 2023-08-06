package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.model.SectionDto;

import java.util.List;

public interface SectionService {
    List<SectionDto> getSection(String id) throws NotFoundException, ForbiddenException;

    List<SectionDto> getSection(String sectionId, String userId) throws NotFoundException, ForbiddenException;

    SectionDto createSubSection(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, NotAllowedException, ForbiddenException;

    SectionDto createRevision(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, ForbiddenException;

    void deleteSection(String id, String userId) throws NotFoundException, NotAllowedException, ForbiddenException;
}
