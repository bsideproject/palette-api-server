package com.palette.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {
    private String url;

    public static UploadResponse of(String url) {
        return UploadResponse.builder().url(url).build();
    }
}
