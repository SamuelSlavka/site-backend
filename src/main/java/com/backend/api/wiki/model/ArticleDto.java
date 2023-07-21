package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private String id;

    private String title;

    private SectionDto section;

    private String createdBy;

    private Boolean isPrivate = Boolean.FALSE;
}
