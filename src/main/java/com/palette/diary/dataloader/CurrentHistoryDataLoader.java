package com.palette.diary.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import com.palette.diary.domain.History;
import com.palette.diary.repository.query.DiaryQueryRepository;
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
@DgsDataLoader(name = "currentHistory")
@Component
@RequiredArgsConstructor
public class CurrentHistoryDataLoader implements MappedBatchLoader<Long, History> {

    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public CompletionStage<Map<Long, History>> load(Set<Long> diaryIds) {
        log.info("CurrentHistoryDataLoader call");
        log.info("diaries size: {}", diaryIds.size());
        Map<Long, History> maps = new HashMap<>();

        List<History> currentHistories = diaryQueryRepository.findProgressHistory(
            diaryIds.stream().toList());

        log.info("currentHistories size: {}", currentHistories.size());

        for (History currentHistory : currentHistories) {
            Long diaryId = currentHistory.getDiary().getId();
            if (maps.get(diaryId) == null) {
                maps.put(diaryId, currentHistory);
            } else {
                maps.put(diaryId, null);
            }
        }
        return CompletableFuture.supplyAsync(() -> maps);
    }

}
