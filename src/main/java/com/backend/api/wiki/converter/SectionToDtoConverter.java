package com.backend.api.wiki.converter;

import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.model.RevisionDto;
import com.backend.api.wiki.model.SectionDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SectionToDtoConverter implements Converter<Section, SectionDto> {

    @Override
    public SectionDto convert(Section from) {
        return new SectionDto(from.getId(),
                this.convertRevision(from.getLatestRevision()),
                convertSets(from.getSubsections()),
                from.getSectionOrder(),
                from.getDepth(),
                from.getArticle().getCreatedBy());
    }

    private Set<SectionDto> convertSets(Set<Section> from) {
        if (Objects.nonNull(from)) {
            return from.stream().map(this::convert).collect(Collectors.toSet());
        }
        return null;
    }

    private RevisionDto convertRevision(Revision from) {
        if (Objects.nonNull(from)) {
            return new RevisionDto(from.getId(), from.getText());
        }
        return null;
    }
}
