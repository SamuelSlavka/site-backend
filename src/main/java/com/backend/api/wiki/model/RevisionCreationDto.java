package com.backend.api.wiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
public class RevisionCreationDto {
    @NonNull
    private String title;
    @NonNull
    private String text;
}
