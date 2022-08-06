package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.SocialType;
import com.palette.user.domain.User;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiaryInput {

    private String title;
    private String color;

    public Diary toEntity(String invitationCode) {
        return Diary.builder()
            .title(this.title)
            .invitationCode(invitationCode)
            .color(this.color)
            .build();
    }

    //TODO: 토큰 파싱 후 추출한 데이터로 User객체 생성 후 인자로 전달
    public DiaryGroup toEntity(Diary diary) {
        return DiaryGroup.builder()
            .user(User.builder()
                .id(1L)
                .email("wlsgmdchemd@naver.com")
                .socialTypes(List.of(SocialType.KAKAO))
                .build()
            )
            .diary(diary)
            .isAdmin(1)
            .build();
    }

}
