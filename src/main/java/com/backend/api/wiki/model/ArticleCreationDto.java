package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleCreationDto {
    private String title;
    private Boolean isPrivate;
}
