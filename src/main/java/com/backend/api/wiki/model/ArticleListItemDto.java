package com.backend.api.wiki.model;

import com.backend.api.wiki.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListItemDto {
    private String id;
    private String title;
    private String section;
    private String createdBy;
    private Boolean isPrivate;
    private Boolean isPubliclyEditable;
    private List<Category> categories;
}
