package com.palette.diary.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.palette.color.domain.Color;
import com.palette.color.repository.ColorRepository;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.diary.fetcher.dto.CreateDiaryInput;
import com.palette.diary.fetcher.dto.CreateDiaryOutput;
import com.palette.diary.fetcher.dto.DiaryDateInput;
import com.palette.diary.fetcher.dto.DiaryDateOutput;
import com.palette.diary.fetcher.dto.InviteDiaryInput;
import com.palette.diary.fetcher.dto.InviteDiaryOutput;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import com.palette.diary.repository.HistoryRepository;
import com.palette.resolver.Authentication;
import com.palette.resolver.LoginUser;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class DiaryFetcher {

    private final DiaryRepository diaryRepository;
    private final DiaryGroupRepository diaryGroupRepository;
    private final HistoryRepository historyRepository;
    //TODO: 서비스 혹은 Component 패키지 생성 시 다른 도메인을 호출하는 패키지 위치 고민
    private final UserRepository userRepository;
    private final ColorRepository colorRepository;

    @Authentication
    @DgsMutation
    @Transactional
    public CreateDiaryOutput createDiary(@InputArgument CreateDiaryInput createDiaryInput,
        LoginUser loginUser) {
        Color color = colorRepository.findById(createDiaryInput.getColorId())
            .orElseThrow(IllegalArgumentException::new);//TODO: 추후 예외처리

        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(IllegalArgumentException::new); //TODO: 추후 예외처리
        String invitationCode = RandomStringUtils.randomAlphabetic(8);
        Diary diary = diaryRepository.save(createDiaryInput.toEntity(invitationCode, color));
        diaryGroupRepository.save(createDiaryInput.toEntity(diary, user));
        return CreateDiaryOutput.of(diary.getInvitationCode());
    }

    /**
     * 예외 종류 한 그룹에 2명이상 존재시 예외처리, 기존에 나간 회원일 경우 예외처리
     */
    @Authentication
    @DgsMutation
    public InviteDiaryOutput inviteDiary(@InputArgument InviteDiaryInput inviteDiaryInput,
        LoginUser loginUser) {
        //TODO: join 사용하여 쿼리 한번으로 축소 가능성 검토
        Diary diary = diaryRepository.findByInvitationCode(inviteDiaryInput.getInvitationCode())
            .orElseThrow(() -> new IllegalArgumentException()); //TODO: 상세 예외처리(해당하는 초대코드 미존재)

        DiaryGroup diaryGroup = diaryGroupRepository.findByDiaryAndIsAdmin(diary, 1)
            .orElseThrow(() -> new IllegalArgumentException());//TODO: 상세 예외처리

        User adminUser = diaryGroup.getUser();

        User invitedUser = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(IllegalArgumentException::new); //TODO: 추후 예외처리

        diaryGroupRepository.save(InviteDiaryInput.of(invitedUser, diary));

        return InviteDiaryOutput.of(adminUser, diary);
    }

    @DgsMutation
    public DiaryDateOutput updateDiaryDate(@InputArgument DiaryDateInput diaryDateInput) {
        Diary diary = diaryRepository.findById(diaryDateInput.getDiaryId())
            .orElseThrow(IllegalArgumentException::new);//TODO: 추후 예외처리
        History history = historyRepository.save(diaryDateInput.toEntity(diary));

        return DiaryDateOutput.builder()
            .historyId(history.getId())
            .build();
    }

}

