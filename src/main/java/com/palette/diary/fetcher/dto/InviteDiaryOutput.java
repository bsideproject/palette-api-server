package com.palette.diary.fetcher.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InviteDiaryOutput {

    private String nickname;
    private String title;

    public static InviteDiaryOutput of(String nickname, String title) {
        return InviteDiaryOutput.builder()
            .nickname("닉네임") //TODO: User 엔티티 필드 추가 후 수정
            .title(title)
            .build();
    }

}
