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
import com.palette.diary.fetcher.dto.CreateDiaryInput;
import com.palette.diary.fetcher.dto.CreateDiaryOutput;
import com.palette.diary.fetcher.dto.CreateHistoryInput;
import com.palette.diary.fetcher.dto.CreateHistoryOutput;
import com.palette.diary.fetcher.dto.CreatePageInput;
import com.palette.diary.fetcher.dto.InviteDiaryInput;
import com.palette.diary.fetcher.dto.InviteDiaryOutput;
import com.palette.diary.fetcher.dto.PageQueryInput;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import com.palette.diary.repository.HistoryRepository;
import com.palette.diary.repository.PageRepository;
import com.palette.exception.graphql.ColorNotFoundException;
import com.palette.exception.graphql.DiaryExistUserException;
import com.palette.exception.graphql.DiaryNotFoundException;
import com.palette.exception.graphql.DiaryOutedUserException;
import com.palette.exception.graphql.DiaryOverUserException;
import com.palette.exception.graphql.InviteCodeNotFoundException;
import com.palette.exception.graphql.ProgressedHistoryException;
import com.palette.exception.graphql.UserNotFoundException;
import com.palette.resolver.Authentication;
import com.palette.resolver.LoginUser;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    /**
     * GlobalErrorType 참고
     *
     * @throws ColorNotFoundException
     * @throws UserNotFoundException
     */
    @Authentication
    @DgsMutation
    @Transactional
    public CreateDiaryOutput createDiary(@InputArgument CreateDiaryInput createDiaryInput,
        LoginUser loginUser) {
        Color color = colorRepository.findById(createDiaryInput.getColorId())
            .orElseThrow(ColorNotFoundException::new);

        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundException::new);
        String invitationCode = RandomStringUtils.randomAlphabetic(8);
        Diary diary = diaryRepository.save(createDiaryInput.toEntity(invitationCode, color));
        diaryGroupRepository.save(createDiaryInput.toEntity(diary, user));
        return CreateDiaryOutput.of(diary.getInvitationCode());
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws InviteCodeNotFoundException
     * @throws UserNotFoundException
     * @throws DiaryNotFoundException
     * @throws DiaryOverUserException
     * @throws DiaryOutedUserException
     * @throws DiaryExistUserException
     */
    @Authentication
    @Transactional
    @DgsMutation
    public InviteDiaryOutput inviteDiary(@InputArgument InviteDiaryInput inviteDiaryInput,
        LoginUser loginUser) {
        Diary diary = diaryRepository.findByInvitationCode(inviteDiaryInput.getInvitationCode())
            .orElseThrow(InviteCodeNotFoundException::new);

        List<DiaryGroup> diaryGroups = diaryGroupRepository.findByDiary(diary);

        User invitedUser = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundException::new);

        if (diaryGroups.isEmpty()) {
            throw new DiaryNotFoundException();
        }

        if (diaryGroups.size() >= 2) {
            throw new DiaryOverUserException();
        }

        for (DiaryGroup diaryGroup : diaryGroups) {
            User user = diaryGroup.getUser();

            if (user.getEmail().equals(loginUser.getEmail()) && diaryGroup.getIsOuted()) {
                throw new DiaryOutedUserException();
            }

            if (user.getEmail().equals(loginUser.getEmail())) {
                throw new DiaryExistUserException();
            }
        }

        User adminUser = diaryGroups.stream()
            .filter(DiaryGroup::getIsAdmin)
            .map(DiaryGroup::getUser)
            .findAny()
            .orElse(null);

        diaryGroupRepository.save(InviteDiaryInput.of(invitedUser, diary));

        return InviteDiaryOutput.of(adminUser, diary);
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundException
     * @throws ProgressedHistoryException
     */
    @DgsMutation
    @Transactional
    public CreateHistoryOutput createHistory(@InputArgument CreateHistoryInput createHistoryInput) {
        Diary diary = diaryRepository.findById(createHistoryInput.getDiaryId())
            .orElseThrow(UserNotFoundException::new);

        History progressHistory = historyRepository.findProgressHistory(diary);
        if (progressHistory != null) {
            throw new ProgressedHistoryException();
        }

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
        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(); // TODO: UserNotFoundException
        History history = historyRepository.findById(createPageInput.getHistoryId())
            .orElseThrow(); // TODO: HistoryNotFoundException
        Page page = Page.builder()
            .title(createPageInput.getTitle())
            .body(createPageInput.getBody())
            .userId(user.getId())
            //.imageUrls(createPageInput.getImageUrls())
            .history(history)
            .build();
        return pageRepository.save(page);
    }

    @DgsData(parentType = "Page", field = "author")
    public User getPageAuthor(DgsDataFetchingEnvironment dfe) {
        Page page = dfe.getSource();
        return userRepository.getById(page.getUserId());
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundException
     */
    @Authentication
    @DgsQuery(field = "diaries")
    public List<Diary> getDiary(LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundException::new);

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

    @DgsData(parentType = "Diary", field = "joinedUsers")
    public List<User> getJoinedUsers(DgsDataFetchingEnvironment dfe) {
        Diary diary = dfe.getSource();
        return diaryGroupRepository.findByDiary(diary).stream()
            .map(DiaryGroup::getUser)
            .collect(Collectors.toList());
    }

    @DgsData(parentType = "History", field = "remainingDays")
    public int getPeriodDays(DgsDataFetchingEnvironment dfe) {
        History history = dfe.getSource();
        if (!history.getIsDeleted()) {
            return (int) ChronoUnit.DAYS.between(LocalDateTime.now(), history.getEndDate());
        }

        return 0;
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

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundException
     */
    @DgsData(parentType = "Page", field = "author")
    public User getAuthor(DgsDataFetchingEnvironment dfe) {
        Page page = dfe.getSource();
        Long userId = page.getUserId();
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
    }

    //과연 토큰을 받을 수 있을 것인가
    @Authentication
    @DgsData(parentType = "Page", field = "isSelf")
    public Boolean getIsSelf(DgsDataFetchingEnvironment dfe, LoginUser loginUser) {
        Page page = dfe.getSource();
        Long authorId = page.getUserId();
        log.info("currentUser: {} ", loginUser);
        log.info("page authorId: {} ", authorId);
        if (Objects.equals(loginUser.getUserId(), authorId)) {
            return true;
        }
        return false;
    }

}
