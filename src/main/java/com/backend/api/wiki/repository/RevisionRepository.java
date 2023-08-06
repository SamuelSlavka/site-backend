package com.backend.api.wiki.repository;

import com.backend.api.core.repository.OwnedRepository;
import com.backend.api.wiki.entity.Revision;
import org.springframework.stereotype.Repository;

@Repository
public interface RevisionRepository extends OwnedRepository<Revision, String> {

}
