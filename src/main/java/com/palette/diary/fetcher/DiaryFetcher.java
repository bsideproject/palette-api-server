package com.palette.diary.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.palette.color.domain.Color;
import com.palette.color.repository.ColorRepository;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.fetcher.dto.CreateDiaryInput;
import com.palette.diary.fetcher.dto.CreateDiaryOutput;
import com.palette.diary.fetcher.dto.InviteDiaryInput;
import com.palette.diary.fetcher.dto.InviteDiaryOutput;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
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

    @DgsMutation
    public InviteDiaryOutput inviteDiary(@InputArgument InviteDiaryInput inviteDiaryInput) {
        //TODO: join 사용하여 쿼리 한번으로 축소 가능성 검토
        Diary diary = diaryRepository.findByInvitationCode(inviteDiaryInput.getInvitationCode())
            .orElseThrow(() -> new IllegalArgumentException()); //TODO: 예외처리

        DiaryGroup diaryGroup = diaryGroupRepository.findByDiaryAndIsAdmin(diary, 1)
            .orElseThrow(() -> new IllegalArgumentException());//TODO: 예외처리;

        User adminUser = diaryGroup.getUser();

        //TODO: 토큰 파싱 후 추출한 데이터로 User객체 생성 후 인자로 전달
        //현재는 일기를 생성한 유저 객체 삽입
        diaryGroupRepository.save(InviteDiaryInput.of(adminUser, diary));

        return InviteDiaryOutput.of("nickname", diary.getTitle());
    }

}

