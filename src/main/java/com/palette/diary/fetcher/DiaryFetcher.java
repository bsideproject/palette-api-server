package com.palette.diary.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.palette.BaseEntity;
import com.palette.color.domain.Color;
import com.palette.color.repository.ColorRepository;
import com.palette.common.PageInput;
import com.palette.common.S3Properties;
import com.palette.diary.DiaryFetcherDto;
import com.palette.diary.dataloader.CurrentHistoryDataLoader;
import com.palette.diary.dataloader.JoinUserDataLoader;
import com.palette.diary.dataloader.OutedUserDataLoader;
import com.palette.diary.dataloader.PastHistoriesDataLoader;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Image;
import com.palette.diary.domain.Page;
import com.palette.diary.fetcher.dto.CreateDiaryInput;
import com.palette.diary.fetcher.dto.CreateDiaryOutput;
import com.palette.diary.fetcher.dto.CreateHistoryInput;
import com.palette.diary.fetcher.dto.CreateHistoryOutput;
import com.palette.diary.fetcher.dto.CreatePageInput;
import com.palette.diary.fetcher.dto.DeletePageInput;
import com.palette.diary.fetcher.dto.EditPageInput;
import com.palette.diary.fetcher.dto.InviteDiaryInput;
import com.palette.diary.fetcher.dto.InviteDiaryOutput;
import com.palette.diary.fetcher.dto.OutDiaryInput;
import com.palette.diary.fetcher.dto.PageQueryInput;
import com.palette.diary.fetcher.dto.SelectHistoryOutput;
import com.palette.diary.fetcher.dto.TestCreateHistoryInput;
import com.palette.diary.fetcher.dto.UpdateDiaryInput;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import com.palette.diary.repository.HistoryRepository;
import com.palette.diary.repository.PageRepository;
import com.palette.diary.repository.query.DiaryQueryRepository;
import com.palette.diary.service.DiaryService;
import com.palette.event.Events;
import com.palette.event.EventsKind;
import com.palette.event.PushAlarmEvent;
import com.palette.event.PushAlarmEventDto;
import com.palette.exception.graphql.ColorNotFoundException;
import com.palette.exception.graphql.DiaryDiscardException;
import com.palette.exception.graphql.DiaryExistUserException;
import com.palette.exception.graphql.DiaryNotFoundException;
import com.palette.exception.graphql.DiaryOutedUserException;
import com.palette.exception.graphql.DiaryOverUserException;
import com.palette.exception.graphql.HistoryNotFoundException;
import com.palette.exception.graphql.InviteCodeNotFoundException;
import com.palette.exception.graphql.PageNotFoundException;
import com.palette.exception.graphql.PermissionDeniedException;
import com.palette.exception.graphql.ProgressedHistoryException;
import com.palette.exception.graphql.UserNotFoundExceptionForGraphQL;
import com.palette.resolver.Authentication;
import com.palette.resolver.LoginUser;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import graphql.execution.DataFetcherResult;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.dataloader.DataLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class DiaryFetcher {

    private final DiaryRepository diaryRepository;
    private final DiaryGroupRepository diaryGroupRepository;
    private final DiaryQueryRepository diaryQueryRepository;
    //TODO: 서비스 혹은 Component 패키지 생성 시 다른 도메인을 호출하는 패키지 위치 고민
    private final UserRepository userRepository;
    private final ColorRepository colorRepository;
    private final HistoryRepository historyRepository;
    private final PageRepository pageRepository;
    private final DiaryService diaryService;

    /**
     * GlobalErrorType 참고
     *
     * @throws ColorNotFoundException
     * @throws UserNotFoundExceptionForGraphQL
     */
    @Authentication
    @DgsMutation
    @Transactional
    public CreateDiaryOutput createDiary(@InputArgument CreateDiaryInput createDiaryInput,
        LoginUser loginUser) {
        Color color = colorRepository.findById(createDiaryInput.getColorId())
            .orElseThrow(ColorNotFoundException::new);

        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);
        String invitationCode = RandomStringUtils.randomAlphabetic(8);
        Diary diary = diaryRepository.save(createDiaryInput.toEntity(invitationCode, color));
        diaryGroupRepository.save(createDiaryInput.toEntity(diary, user));
        return CreateDiaryOutput.of(diary.getInvitationCode());
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws InviteCodeNotFoundException
     * @throws UserNotFoundExceptionForGraphQL
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

        List<DiaryGroup> diaryGroups = diaryQueryRepository.findByDiary(diary);

        User invitedUser = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);

        if (diaryGroups.isEmpty()) {
            throw new DiaryNotFoundException();
        }

        if (diaryGroups.size() >= 2) {
            throw new DiaryOverUserException();
        }

        for (DiaryGroup diaryGroup : diaryGroups) {
            User user = diaryGroup.getUser();
            Boolean isOuted = diaryGroup.getIsOuted();

            if (isOuted) {
                throw new DiaryDiscardException();
            }

            if (user.getEmail().equals(loginUser.getEmail()) && diaryGroup.getIsOuted()) {
                throw new DiaryOutedUserException();
            }

            if (user.getEmail().equals(loginUser.getEmail())) {
                throw new DiaryExistUserException();
            }
        }

        diaryGroupRepository.save(InviteDiaryInput.of(invitedUser, diary));

        User adminUser = diaryGroups.stream()
            .filter(DiaryGroup::getIsAdmin)
            .map(DiaryGroup::getUser)
            .findAny()
            .orElse(null);

        PushAlarmEventDto eventDto = PushAlarmEventDto.builder()
            .eventsKind(EventsKind.CREATE_DIARY)
            .diary(diary)
            .userIds(List.of(adminUser.getId(), invitedUser.getId()))
            .build();

        Events.raise(new PushAlarmEvent(eventDto));

        return InviteDiaryOutput.of(adminUser, diary);
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws DiaryNotFoundException
     * @throws ProgressedHistoryException
     */
    @DgsMutation
    @Transactional
    public CreateHistoryOutput createHistory(@InputArgument CreateHistoryInput createHistoryInput) {
        Diary diary = diaryRepository.findById(createHistoryInput.getDiaryId())
            .orElseThrow(DiaryNotFoundException::new);

        History progressHistory = diaryQueryRepository.findProgressHistory(diary);
        if (progressHistory != null) {
            throw new ProgressedHistoryException();
        }

        History history = historyRepository.save(createHistoryInput.toEntity(diary));
        diaryService.registerHistoryFinishedJob(history);
        diaryService.registerHistoryRemindOneJob(history);
        diaryService.registerHistoryRemindTwoJob(history);

        PushAlarmEventDto eventDto = PushAlarmEventDto.builder()
            .eventsKind(EventsKind.CREATE_HISTORY)
            .history(history)
            .build();

        Events.raise(new PushAlarmEvent(eventDto));

        return CreateHistoryOutput.builder()
            .historyId(history.getId())
            .build();
    }

    @DgsMutation
    @Transactional
    public CreateHistoryOutput testCreateHistory(
        @InputArgument TestCreateHistoryInput testCreateHistoryInput) {
        Diary diary = diaryRepository.findById(testCreateHistoryInput.getDiaryId())
            .orElseThrow(DiaryNotFoundException::new);

        History history = historyRepository.save(testCreateHistoryInput.toEntity(diary));
        diaryService.registerHistoryFinishedJob(history);

        PushAlarmEventDto eventDto = PushAlarmEventDto.builder()
            .eventsKind(EventsKind.CREATE_HISTORY)
            .history(history)
            .build();

        Events.raise(new PushAlarmEvent(eventDto));

        return CreateHistoryOutput.builder()
            .historyId(history.getId())
            .build();
    }

    @Authentication
    @Transactional
    @DgsData(parentType = "Mutation", field = "createPage")
    public Page createPage(@InputArgument CreatePageInput createPageInput, LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);
        History history = historyRepository.findById(createPageInput.getHistoryId())
            .orElseThrow(HistoryNotFoundException::new);
        Page page = Page.builder()
            .title(createPageInput.getTitle())
            .body(createPageInput.getBody())
            .userId(user.getId())
            .history(history)
            .build();

        List<Image> images = new ArrayList<>();

        createPageInput.getImageUrls().forEach(imageUrl -> {
            String path = imageUrl.substring(imageUrl.indexOf("com") + 3);
            images.add(
                Image.builder()
                    .page(page)
                    .domain(S3Properties.domain)
                    .path(path)
                    .build()
            );
        });

        images.forEach(page::addImage);

        PushAlarmEventDto eventDto = PushAlarmEventDto.builder()
            .eventsKind(EventsKind.CREATE_PAGE)
            .page(page)
            .build();

        Events.raise(new PushAlarmEvent(eventDto));

        return pageRepository.save(page);
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundExceptionForGraphQL
     */
    @Authentication
    @DgsQuery(field = "diaries")
    public DataFetcherResult<List<Diary>> getDiary(@InputArgument PageInput pageInput,
        LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);

        Integer offset = pageInput.getDiaryOffset();
        Integer size = pageInput.getDiarySize();
        Integer pageOffset = pageInput.getPageOffset();
        Integer pageSize = pageInput.getPageSize();

        DiaryFetcherDto diaryFetcherDto = DiaryFetcherDto.builder()
            .pageOffset(pageOffset)
            .pageSize(pageSize)
            .loginUser(loginUser)
            .build();

        List<Diary> diaries = diaryQueryRepository.findByUser(user, PageRequest.of(offset, size))
            .stream()
            .map(DiaryGroup::getDiary)
            .collect(Collectors.toList());

        return DataFetcherResult.<List<Diary>>newResult()
            .data(diaries)
            .localContext(diaryFetcherDto)
            .build();
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundExceptionForGraphQL
     */
    @Authentication
    @DgsQuery(field = "histories")
    public DataFetcherResult<SelectHistoryOutput> getHistories(@InputArgument Long diaryId,
        @InputArgument PageInput pageInput, LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);

        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(DiaryNotFoundException::new);

        Integer offset = pageInput.getHistoryOffset();
        Integer size = pageInput.getHistorySize();
        Integer pageOffset = pageInput.getPageOffset();
        Integer pageSize = pageInput.getPageSize();

        DiaryFetcherDto diaryFetcherDto = DiaryFetcherDto.builder()
            .pageOffset(pageOffset)
            .pageSize(pageSize)
            .loginUser(loginUser)
            .build();

        List<History> histories = diaryQueryRepository.findHistories(user, diary,
            PageRequest.of(offset, size));

        SelectHistoryOutput selectHistoryOutput = SelectHistoryOutput.builder()
            .diaryTitle(diary.getTitle())
            .histories(histories)
            .build();

        return DataFetcherResult.<SelectHistoryOutput>newResult()
            .data(selectHistoryOutput)
            .localContext(diaryFetcherDto)
            .build();
    }

    @DgsData(parentType = "Diary", field = "currentHistory")
    public CompletableFuture<History> getCurrentHistory(DgsDataFetchingEnvironment dfe) {
        Diary diary = dfe.getSource();

        DataLoader<Long, History> dataLoader = dfe.getDataLoader(CurrentHistoryDataLoader.class);

        return dataLoader.load(diary.getId());
    }

    @DgsData(parentType = "Diary", field = "pastHistories")
    public CompletableFuture<List<History>> getPastHistories(DgsDataFetchingEnvironment dfe) {
        Diary diary = dfe.getSource();

        DataLoader<Long, List<History>> dataLoader = dfe.getDataLoader(
            PastHistoriesDataLoader.class);

        return dataLoader.load(diary.getId());
    }

    @DgsData(parentType = "Diary", field = "joinedUsers")
    public CompletableFuture<List<User>> getJoinedUsers(DgsDataFetchingEnvironment dfe) {
        DataLoader<Long, List<User>> dataLoader = dfe.getDataLoader(JoinUserDataLoader.class);

        Diary diary = dfe.getSource();

        return dataLoader.load(diary.getId());
    }

    @DgsData(parentType = "Diary", field = "outedUser")
    public CompletableFuture<String> getOutedUser(DgsDataFetchingEnvironment dfe) {
        DataLoader<Long, String> dataLoader = dfe.getDataLoader(OutedUserDataLoader.class);

        Diary diary = dfe.getSource();

        return dataLoader.load(diary.getId());
    }

    @DgsData(parentType = "History", field = "remainingDays")
    public int getPeriodDays(DgsDataFetchingEnvironment dfe) {
        History history = dfe.getSource();
        if (!history.getIsDeleted()) {
            return (int) ChronoUnit.DAYS.between(LocalDateTime.now(), history.getEndDate()) + 1;
        }

        return 0;
    }

    @DgsData(parentType = "Diary", field = "diaryStatus")
    public String getDiaryStatus(DgsDataFetchingEnvironment dfe) {
        Diary diary = dfe.getSource();
        History history = diaryQueryRepository.findProgressHistory(diary);
        List<DiaryGroup> diaryGroups = diaryQueryRepository.findByDiary(diary);
        if (diaryGroups.isEmpty()) {
            return "";
        }

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
    @Transactional(readOnly = true)
    public List<Page> getPages(DgsDataFetchingEnvironment dfe) {
        History history = dfe.getSource();
        DiaryFetcherDto diaryFetcherDto = dfe.getLocalContext();

        if (diaryFetcherDto != null) {
            Integer offset = diaryFetcherDto.getPageOffset();
            Integer size = diaryFetcherDto.getPageSize();
            log.info("offset: {}", offset);
            log.info("size: {}", size);

            List<Page> pages = diaryQueryRepository.findPage(history, PageRequest.of(offset, size));
            List<Long> pageIds = pages.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

            return diaryQueryRepository.findPageByIds(pageIds);
        } else {
            return diaryQueryRepository.findPage(history);
        }
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundExceptionForGraphQL
     */
    @DgsData(parentType = "Page", field = "author")
    public User getAuthor(DgsDataFetchingEnvironment dfe) {
        Page page = dfe.getSource();
        Long userId = page.getUserId();
        return userRepository.findById(userId)
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);
    }

    @DgsData(parentType = "Page", field = "isSelf")
    public Boolean getIsSelf(DgsDataFetchingEnvironment dfe) {
        Page page = dfe.getSource();
        Long authorId = page.getUserId();
        DiaryFetcherDto diaryFetcherDto = dfe.getLocalContext();
        LoginUser loginUser = diaryFetcherDto.getLoginUser();

        log.info("currentUser: {} ", loginUser);
        log.info("page authorId: {} ", authorId);
        if (Objects.equals(loginUser.getUserId(), authorId)) {
            return true;
        }
        return false;
    }

    @DgsData(parentType = "Page", field = "images")
    public List<Image> getImages(DgsDataFetchingEnvironment dfe) {
        Page page = dfe.getSource();
        return page.getImages();
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws DiaryNotFoundException
     */
    @DgsMutation
    @Transactional
    public Boolean updateDiary(@InputArgument UpdateDiaryInput updateDiaryInput) {
        Diary diary = diaryRepository.findById(updateDiaryInput.getDiaryId())
            .orElseThrow(DiaryNotFoundException::new);
        String title = updateDiaryInput.getTitle();
        Long colorId = updateDiaryInput.getColorId();
        boolean isUpdated = false;

        if (StringUtils.hasText(title)) {
            diary.changeTitle(updateDiaryInput.getTitle());
            isUpdated = true;
        }

        if (colorId != null) {
            Color color = colorRepository.findById(updateDiaryInput.getColorId())
                .orElseThrow(ColorNotFoundException::new);
            diary.changeColor(color);
            isUpdated = true;
        }

        return isUpdated;
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws DiaryNotFoundException
     * @throws UserNotFoundExceptionForGraphQL
     */
    @DgsMutation
    @Authentication
    @Transactional
    public Boolean outDiary(@InputArgument OutDiaryInput outDiaryInput, LoginUser loginUser) {
        Diary diary = diaryRepository.findById(outDiaryInput.getDiaryId())
            .orElseThrow(DiaryNotFoundException::new);

        User user = userRepository.findById(loginUser.getUserId())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);

        DiaryGroup diaryGroup = diaryGroupRepository.findByDiaryAndUser(diary, user)
            .orElseThrow(DiaryNotFoundException::new);

        PushAlarmEventDto eventDto = PushAlarmEventDto.builder()
            .eventsKind(EventsKind.OUT_DIARY)
            .diary(diary)
            .user(user)
            .build();

        Events.raise(new PushAlarmEvent(eventDto));

        diaryGroup.userOut();

        return true;
    }

    @Authentication
    @DgsData(parentType = "Query", field = "page")
    public Page getPage(@InputArgument PageQueryInput pageQueryInput, LoginUser loginUser) {
        Page page = pageRepository.findById(pageQueryInput.getId())
            .orElseThrow(PageNotFoundException::new);
        List<User> users = userRepository.findUsers(page);
        if (users.stream().anyMatch(user -> user.getEmail().equals(loginUser.getEmail()))) {
            return page;
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Authentication
    @DgsMutation
    public Boolean deletePage(@InputArgument DeletePageInput deletePageInput, LoginUser loginUser) {
        Page page = pageRepository.findById(deletePageInput.getPageId())
            .orElseThrow(PageNotFoundException::new);
        if (loginUser.getUserId().equals(page.getUserId())) {
            pageRepository.delete(page);
            return true;
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Authentication
    @Transactional
    @DgsMutation
    public Page editPage(@InputArgument EditPageInput editPageInput, LoginUser loginUser) {
        Page page = pageRepository.findById(editPageInput.getPageId())
            .orElseThrow(PageNotFoundException::new);
        if (!page.getUserId().equals(loginUser.getUserId())) {
            throw new PermissionDeniedException();
        }
        String title = editPageInput.getTitle();
        String body = editPageInput.getBody();
        List<String> imageUrls = editPageInput.getImageUrls();

        if (title != null) {
            page.setTitle(title);
        }

        if (body != null) {
            page.setBody(body);
        }

        if (imageUrls != null) {
            page.clearImages();

            imageUrls.forEach(url -> {
                String path = url.substring(url.indexOf("com") + 3);
                Image image = Image.builder()
                    .page(page)
                    .domain(S3Properties.domain)
                    .path(path)
                    .build();
                page.addImage(image);
            });
        }
        return page;
    }

}
