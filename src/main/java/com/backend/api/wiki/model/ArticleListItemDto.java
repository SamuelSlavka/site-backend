package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListItemDto {

    private String id;

    private String title;

    private String superSection;

}
