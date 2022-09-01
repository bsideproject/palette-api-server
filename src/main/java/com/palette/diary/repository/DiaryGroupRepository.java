package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryGroupRepository extends JpaRepository<DiaryGroup, Long> {

    Optional<DiaryGroup> findByUserId(String userId);

    Optional<DiaryGroup> findByDiaryAndUser(Diary diary, User user);

    List<DiaryGroup> findByDiary(Diary diary);

}
