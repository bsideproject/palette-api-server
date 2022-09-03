package com.palette.diary.fetcher.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDiaryInput {

    private Long diaryId;
    private Long colorId;
    private String title;

}
