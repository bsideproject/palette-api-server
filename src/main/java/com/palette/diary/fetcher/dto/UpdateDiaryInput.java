package com.palette.diary.fetcher.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDiaryInput {

    private Long diaryId;
    private String title;
}
