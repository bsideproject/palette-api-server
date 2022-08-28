package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHistoryInput {

    private Long diaryId;
    private Integer period;

    public History toEntity(Diary diary) {
        return History.builder()
            .diary(diary)
            .startDate(OffsetDateTime.now())
            .endDate(OffsetDateTime.now().plusDays(period))
            .build();
    }

}
