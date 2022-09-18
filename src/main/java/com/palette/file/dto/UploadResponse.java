package com.palette.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {
    private List<String> urls;

    public static UploadResponse of(List<String> urls) {
        return UploadResponse.builder().urls(urls).build();
    }
}
