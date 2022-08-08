package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryGroupRepository extends JpaRepository<DiaryGroup, Long> {

    Optional<DiaryGroup> findByDiaryAndIsAdmin(Diary diary, Integer isAdmin);
}
