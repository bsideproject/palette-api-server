package com.palette.diary.fetcher.dto;

import com.palette.diary.domain.Diary;
import com.palette.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InviteDiaryOutput {

    private User adminUser;
    private Diary diary;

    public static InviteDiaryOutput of(User adminUser, Diary diary) {
        return InviteDiaryOutput.builder()
            .adminUser(adminUser)
            .diary(diary)
            .build();
    }

}
