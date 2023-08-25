package com.backend.api.wiki.service;

import com.backend.api.security.error.ForbiddenException;
import com.backend.api.wiki.error.NotAllowedException;
import com.backend.api.wiki.error.NotFoundException;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.model.SectionPaginationDto;

import java.util.List;

/**
 * Service used for section manipulation
 */
public interface SectionService {
    /**
     * Returns list of sections until given depth from public article
     *
     * @param id top level section id
     * @return returns list of sections ordered by their depth and creation order
     * @throws NotFoundException  thrown when article is not found
     * @throws ForbiddenException thrown when user doesn't have permission for this action
     */
    List<SectionDto> getPublicSection(String id, SectionPaginationDto page) throws NotFoundException,
            ForbiddenException;

    /**
     * Gets section and its subsections for logged user
     *
     * @param sectionId  top level section id
     * @param userId     id of logged user
     * @param pagination page and offset and depth limit
     * @return returns list of sections ordered by their depth and creation order
     * @throws NotFoundException  thrown when article is not found
     * @throws ForbiddenException thrown when user doesn't have permission for this action
     */
    List<SectionDto> getSection(String sectionId, String userId, SectionPaginationDto pagination) throws NotFoundException, ForbiddenException;

    /**
     * Creates a new subsection
     *
     * @param id              top level section id
     * @param revisionContent object with first revision values
     * @param userId          id of logged user
     * @return returns new subsection
     * @throws NotFoundException  thrown when article is not found
     * @throws ForbiddenException thrown when user doesn't have permission for this action
     */
    SectionDto createSubSection(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException, ForbiddenException;

    /**
     * Creates a new revision on an existing section
     *
     * @param id              top level section id
     * @param revisionContent revision values
     * @param userId          id of logged user
     * @return returns section with new revision
     * @throws NotFoundException  thrown when article is not found
     * @throws ForbiddenException thrown when user doesn't have permission for this action
     */
    SectionDto createRevision(String id, RevisionCreationDto revisionContent, String userId) throws NotFoundException
            , ForbiddenException;

    /**
     * Soft deletes section
     *
     * @param id     top level section id
     * @param userId id of logged user
     * @throws NotAllowedException thrown if the user cant delete subsections
     * @throws NotFoundException   thrown when article is not found
     * @throws ForbiddenException  thrown when user doesn't have permission for this action
     */
    void deleteSection(String id, String userId) throws NotFoundException, NotAllowedException, ForbiddenException;
}
