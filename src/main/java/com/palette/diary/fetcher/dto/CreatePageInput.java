package com.palette.diary.fetcher.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CreatePageInput {
    private String title;
    private String body;
    private Long historyId;
    private ArrayList<String> imageUrls;
}
