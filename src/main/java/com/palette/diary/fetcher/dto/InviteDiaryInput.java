package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteDiaryInput {

    private String invitationCode;

    public static DiaryGroup of(User user, Diary diary) {
        return DiaryGroup.builder()
            .user(user)
            .diary(diary)
            .isAdmin(0)
            .build();
    }

}
