package com.palette.diary.dataloader.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "diaryId")
@Getter
@Setter
@Builder
public class PastHistoriesDto {

    Long diaryId;
    Integer pageSize;
}
