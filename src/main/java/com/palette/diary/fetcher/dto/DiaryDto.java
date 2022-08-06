package com.palette.diary.fetcher.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DiaryDto {

    private String title;
    private String color;
    private String invitationCode;

    public static DiaryDto toCreateDto(String invitationCode) {
        return DiaryDto.builder()
            .invitationCode(invitationCode)
            .build();
    }

}
