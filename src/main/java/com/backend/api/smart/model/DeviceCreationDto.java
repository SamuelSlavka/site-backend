package com.backend.api.smart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCreationDto {
    @NonNull
    String name;
    Boolean isMain;
}
