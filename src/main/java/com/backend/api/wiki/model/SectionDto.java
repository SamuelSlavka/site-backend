package com.backend.api.wiki.model;

import com.backend.api.wiki.entity.Section;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
    private String id;
    private String latestRevision;
    private String superSection;
    private Integer sectionOrder;
    private Integer depth;
    private String createdBy;
    private String article;
    private String text;
    private String title;

    public SectionDto(Section section) {
        this.id = section.getId();
        this.latestRevision = section.getLatestRevision().getId();
        this.superSection = Objects.isNull(section.getSuperSection()) ? null : section.getSuperSection().getId();
        this.sectionOrder = section.getSectionOrder();
        this.depth = section.getDepth();
        this.createdBy = section.getCreatedBy();
        this.article = Objects.isNull(section.getArticle()) ? null : section.getArticle().getId();
        this.text = section.getLatestRevision().getText();
        this.title = section.getLatestRevision().getTitle();
    }
}

