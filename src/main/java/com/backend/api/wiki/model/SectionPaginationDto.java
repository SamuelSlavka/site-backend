package com.backend.api.wiki.model;

import com.backend.api.core.model.PaginationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SectionPaginationDto extends PaginationDto {

    @NonNull
    private Integer limit;

    @NonNull
    private Integer initDepth;

    public SectionPaginationDto(@NonNull Integer page, @NonNull Integer pageSize, @NonNull Integer limit,
                                @NonNull Integer initDepth) {
        super(page, pageSize);
        this.limit = limit;
        this.initDepth = initDepth;
    }
}
