package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
    private String id;

    private RevisionDto latestRevision;

    private Set<SectionDto> subsections;

    private Integer sectionOrder;

    private Integer depth;

    private String createdBy;
}

