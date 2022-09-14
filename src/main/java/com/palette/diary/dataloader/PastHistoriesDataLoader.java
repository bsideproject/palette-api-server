package com.palette.diary.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.repository.query.DiaryQueryRepository;
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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DgsDataLoader(name = "pastHistories")
@Component
@RequiredArgsConstructor
public class PastHistoriesDataLoader implements MappedBatchLoader<Long, List<History>> {

    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public CompletionStage<Map<Long, List<History>>> load(Set<Long> diaryIds) {
        log.info("PastHistoriesDataLoader call");
        Map<Long, List<History>> maps = new HashMap<>();

        Map<Diary, List<History>> collect = diaryQueryRepository.findPastHistories(
                diaryIds.stream().toList()).stream()
            .collect(Collectors.groupingBy(History::getDiary));

        for (Diary diary : collect.keySet()) {
            List<History> histories = collect.get(diary);
            maps.put(diary.getId(), histories);
        }

        return CompletableFuture.supplyAsync(() -> maps);
    }

}
