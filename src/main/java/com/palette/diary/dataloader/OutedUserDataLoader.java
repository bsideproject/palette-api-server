package com.palette.diary.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.repository.query.DiaryQueryRepository;
import com.palette.user.domain.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.MappedBatchLoader;
import org.springframework.stereotype.Component;

@Slf4j
@DgsDataLoader(name = "outedUser")
@Component
@RequiredArgsConstructor
public class OutedUserDataLoader implements MappedBatchLoader<Long, String> {

    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    public CompletionStage<Map<Long, String>> load(Set<Long> diaryIds) {
        log.info("OutedUserDataLoader call");
        Map<Long, String> maps = new HashMap<>();

        Map<Diary, List<DiaryGroup>> collect = diaryQueryRepository.findByDiaryGroup(
                diaryIds.stream().toList()).stream()
            .collect(Collectors.groupingBy(DiaryGroup::getDiary));

        for (Diary diary : collect.keySet()) {
            List<DiaryGroup> diaryGroups = collect.get(diary);
            User outUser = diaryGroups.stream()
                .filter(diaryGroup -> diaryGroup.getIsOuted())
                .map(diaryGroup -> diaryGroup.getUser())
                .findAny()
                .orElse(null);
            String outUsername = "";
            if (outUser != null) {
                outUsername = outUser.getNickname();
            }
            maps.put(diary.getId(), outUsername);
        }

        return CompletableFuture.supplyAsync(() -> maps);
    }

}
