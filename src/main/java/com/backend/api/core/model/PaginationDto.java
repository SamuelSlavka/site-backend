package com.backend.api.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {
    @NonNull
    private Integer page = 0;
    @NonNull
    private Integer pageSize = 10;
}
