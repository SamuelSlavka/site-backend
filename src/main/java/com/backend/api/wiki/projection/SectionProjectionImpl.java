package com.backend.api.wiki.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectionProjectionImpl implements SectionProjection {
    private String id;
    private String latestRevision;
    private String superSection;
    private Integer sectionOrder;
    private Integer depth;
    private String createdBy;
    private String article;
    private String text;
    private String title;
}
