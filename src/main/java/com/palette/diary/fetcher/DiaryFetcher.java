package com.palette.diary.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.palette.color.domain.Color;
import com.palette.color.repository.ColorRepository;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.diary.fetcher.dto.*;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import com.palette.diary.repository.HistoryRepository;
import com.palette.diary.repository.PageRepository;
import com.palette.resolver.Authentication;
import com.palette.resolver.LoginUser;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    private final HistoryRepository historyRepository;

    private final PageRepository pageRepository;

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
    @Transactional
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

    /**
     * TODO: 중복으로 진행중인 교환일기가 있는지 검사
     */
    @DgsMutation
    @Transactional
    public CreateHistoryOutput createHistory(@InputArgument CreateHistoryInput createHistoryInput) {
        Diary diary = diaryRepository.findById(createHistoryInput.getDiaryId())
                .orElseThrow(IllegalArgumentException::new);//TODO: 추후 예외처리
        History history = historyRepository.save(createHistoryInput.toEntity(diary));

        return CreateHistoryOutput.builder()
                .historyId(history.getId())
                .build();
    }

    @DgsData(parentType = "Query", field = "page")
    public Page getPage(@InputArgument PageQueryInput pageQueryInput) {
        return pageRepository.getById(pageQueryInput.getId());
    }

    @Authentication
    @DgsData(parentType = "Mutation", field = "createPage")
    public Page createPage(@InputArgument CreatePageInput createPageInput, LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail()).orElseThrow(); // TODO: UserNotFoundException
        History history = historyRepository.findById(createPageInput.getHistoryId()).orElseThrow(); // TODO: HistoryNotFoundException
        Page page = Page.builder()
                .title(createPageInput.getTitle())
                .body(createPageInput.getBody())
                .author(user)
                .history(history)
                .build();
        return pageRepository.save(page);
    }

    @Authentication
    @DgsQuery(field = "diaries")
    public List<Diary> getDiary(LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(IllegalArgumentException::new); //TODO: 추후 예외처리

        List<Diary> diaries = diaryGroupRepository.findByUser(user).stream()
                .map(DiaryGroup::getDiary)
                .collect(Collectors.toList());

        return diaries;
    }

    @DgsData(parentType = "Diary", field = "currentHistory")
    public History getCurrentHistory(DgsDataFetchingEnvironment dfe) {
        Diary diary = dfe.getSource();
        History history = historyRepository.findProgressHistory(diary);
        if (history == null) {
            return null;
        }
        return history;
    }


    @DgsData(parentType = "Diary", field = "diaryStatus")
    public String getDiaryStatus(DgsDataFetchingEnvironment dfe) {
        Diary diary = dfe.getSource();
        History history = historyRepository.findProgressHistory(diary);
        List<DiaryGroup> diaryGroups = diaryGroupRepository.findByDiary(diary);

        boolean isDiscard = diaryGroups.stream()
                .anyMatch(DiaryGroup::getIsOuted);

        //일기 그룹에 속한 유저가 한명일때
        if (diaryGroups.size() == 1) {
            return "WAIT";
        }

        //일기그룹에서 한명이 나가서 일기그룹이 폐기된 상태일때
        if (diaryGroups.size() == 2 && isDiscard) {
            return "DISCARD";
        }

        //진행중인 히스토리가 없을때
        if (history == null) {
            return "READY";
        } else {  //진행중인 히스토리가 존재할때
            return "START";
        }
    }

    @DgsData(parentType = "History", field = "pages")
    public List<Page> getPages(DgsDataFetchingEnvironment dfe) {
        History history = dfe.getSource();
        return pageRepository.findByHistory(history);
    }
}
