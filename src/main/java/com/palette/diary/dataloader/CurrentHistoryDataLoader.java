package com.palette.diary.dataloader;

import com.netflix.graphql.dgs.DgsDataLoader;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import com.palette.diary.repository.query.DiaryQueryRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.BatchLoader;
import org.springframework.stereotype.Component;

@Slf4j
@DgsDataLoader(name = "currentHistory")
@Component
@RequiredArgsConstructor
public class CurrentHistoryDataLoader implements BatchLoader<Diary, History> {

    private final DiaryQueryRepository diaryQueryRepository;

    @Override
    public CompletionStage<List<History>> load(List<Diary> diaries) {
        log.info("CurrentHistoryDataLoader call");
        log.info("diaries size: {}", diaries.size());
        //List<History> currentHistories = new ArrayList<>();
        //for (Diary diary : diaries) {
        List<History> currentHistories = diaryQueryRepository.findProgressHistory(diaries);
        log.info("currentHistories size: {}", currentHistories.size());

        int count = diaries.size() - currentHistories.size();
        //Error: The size of the promised values MUST be the same size as the key list
        for (int i = 0; i < count; i++) {
            currentHistories.add(null);
        }
//        if (history != null) {
//            currentHistories.add(history);
//        } else {
//            currentHistories.add(null);
//        }
        //}
        return CompletableFuture.supplyAsync(() -> currentHistories);
    }

}
