package com.backend.api.wiki.repository;

import com.backend.api.wiki.entity.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, String> {
}
