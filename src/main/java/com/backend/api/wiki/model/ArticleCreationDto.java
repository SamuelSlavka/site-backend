package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
public class ArticleCreationDto {
    @NonNull
    private String title;
    @NonNull
    private Boolean isPrivate;
    @NonNull
    private Boolean isPubliclyEditable;
}
