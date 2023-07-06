package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
    private String id;

    private RevisionDto latestRevision;

    private List<SectionDto> subsections;

    private Integer sectionOrder;

}
