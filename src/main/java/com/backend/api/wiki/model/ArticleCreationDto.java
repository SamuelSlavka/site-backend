package com.backend.api.wiki.model;

import lombok.Data;

@Data
public class ArticleCreationDto {
    private String title;
    private Boolean isPrivate;
}
