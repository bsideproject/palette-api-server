package com.palette.diary.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.repository.query.DiaryQueryRepository;
import com.palette.user.domain.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DgsDataLoader(name = "joinedUsers")
@Component
@RequiredArgsConstructor
public class JoinUserDataLoader implements MappedBatchLoader<Long, List<User>> {

    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public CompletionStage<Map<Long, List<User>>> load(Set<Long> diaryIds) {
        log.info("JoinUserDataLoader call");
        Map<Long, List<User>> maps = new HashMap<>();

        List<DiaryGroup> diaryGroups = diaryQueryRepository.findByDiary(diaryIds.stream().toList());
        for (DiaryGroup diaryGroup : diaryGroups) {
            Long diaryId = diaryGroup.getDiary().getId();
            User user = diaryGroup.getUser();
            if (maps.get(diaryId) == null) {
                List<User> users = new ArrayList<>();
                maps.put(diaryId, users);
                users.add(user);
            } else {
                List<User> users = maps.get(diaryId);
                users.add(user);
            }
        }

        return CompletableFuture.supplyAsync(() -> maps);
    }

}
