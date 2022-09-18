package com.palette.diary.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import com.palette.BaseEntity;
import com.palette.diary.dataloader.dto.PastHistoriesDto;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import com.palette.diary.fetcher.dto.PastHistory;
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
public class PastHistoriesDataLoader implements MappedBatchLoader<PastHistoriesDto, PastHistory> {

    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public CompletionStage<Map<PastHistoriesDto, PastHistory>> load(
        Set<PastHistoriesDto> pastHistoriesDtos) {
        log.info("PastHistoriesDataLoader call");
        Map<PastHistoriesDto, PastHistory> maps = new HashMap<>();

        List<PastHistoriesDto> collect1 = pastHistoriesDtos.stream().collect(Collectors.toList());
        Integer pageSize = collect1.get(0).getPageSize();

        List<Long> diaryIds = collect1.stream()
            .map(collect -> collect.getDiaryId())
            .collect(Collectors.toList());

        Map<Diary, List<History>> collect = diaryQueryRepository.findPastHistories(
                diaryIds.stream().toList()).stream()
            .collect(Collectors.groupingBy(History::getDiary));

        for (Diary diary : collect.keySet()) {
            List<History> histories = collect.get(diary);
            List<Long> historyIds = histories.stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toList());

            List<Page> pages = diaryQueryRepository.findPage(historyIds).stream()
                .limit(pageSize)
                .toList();

            PastHistory pastHistory = PastHistory.builder()
                .pages(pages)
                .build();

            PastHistoriesDto pastHistoriesDto = PastHistoriesDto.builder()
                .diaryId(diary.getId())
                .pageSize(pageSize)
                .build();

            maps.put(pastHistoriesDto, pastHistory);
        }

        return CompletableFuture.supplyAsync(() -> maps);
    }

}
