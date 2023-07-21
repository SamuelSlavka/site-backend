package com.backend.api.wiki.converter;

import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.model.RevisionDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

public class RevisionToDtoConverter implements Converter<Revision, RevisionDto> {
    @Override
    public RevisionDto convert(Revision from) {
        return new RevisionDto(from.getId(), from.getText());
    }
}
