package com.backend.api.wiki.projection;

import org.springframework.beans.factory.annotation.Value;

public interface SectionProjection {
    String getId();

    @Value("#{target.revision_id}")
    String getLatestRevision();

    @Value("#{target.super_section_id}")
    String getSuperSection();

    @Value("#{target.section_order}")
    Integer getSectionOrder();

    Integer getDepth();

    @Value("#{target.created_by}")
    String getCreatedBy();

    @Value("#{target.article_id}")
    String getArticle();

    String getText();

    String getTitle();

}

