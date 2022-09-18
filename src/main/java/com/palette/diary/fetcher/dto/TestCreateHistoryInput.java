package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestCreateHistoryInput {
    private Long diaryId;
    private Long seconds;

    public History toEntity(Diary diary) {
        return History.builder()
                .diary(diary)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusSeconds(seconds))
                .build();
    }
}
