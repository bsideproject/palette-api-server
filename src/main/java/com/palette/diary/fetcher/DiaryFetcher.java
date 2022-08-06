package com.palette.diary.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import com.palette.diary.domain.Diary;
import com.palette.diary.fetcher.dto.DiaryDto;
import com.palette.diary.fetcher.dto.DiaryInput;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class DiaryFetcher {

    private final DiaryRepository diaryRepository;
    private final DiaryGroupRepository diaryGroupRepository;

    @DgsMutation
    @Transactional
    public DiaryDto createDiary(@InputArgument DiaryInput diaryInput) {
        String invitationCode = RandomStringUtils.randomAlphabetic(8);
        Diary diary = diaryRepository.save(diaryInput.toEntity(invitationCode));
        diaryGroupRepository.save(diaryInput.toEntity(diary));
        return DiaryDto.toCreateDto(diary.getInvitationCode());
    }

}

