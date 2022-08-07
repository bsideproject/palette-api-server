package com.palette.diary.fetcher.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateDiaryOutput {

    private String title;
    private String color;
    private String invitationCode;

    public static CreateDiaryOutput toCreateDto(String invitationCode) {
        return CreateDiaryOutput.builder()
            .invitationCode(invitationCode)
            .build();
    }

}
