package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.History;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelectHistoryOutput {

    private String diaryTitle;
    private List<History> histories;
}
