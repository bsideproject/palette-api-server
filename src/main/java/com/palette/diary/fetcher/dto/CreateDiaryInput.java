package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDiaryInput {

    private String title;
    private String color;

    public Diary toEntity(String invitationCode) {
        return Diary.builder()
            .title(this.title)
            .invitationCode(invitationCode)
            .color(this.color)
            .build();
    }

    public DiaryGroup toEntity(Diary diary, User user) {
        return DiaryGroup.builder()
            .user(user)
            .diary(diary)
            .isAdmin(1)
            .build();
    }

}
