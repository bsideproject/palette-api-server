package com.palette.diary.fetcher.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EditPageInput {
    private Long pageId;
    private String title;
    private String body;
    private List<String> imageUrls;
}
