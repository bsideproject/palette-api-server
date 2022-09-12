package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaryGroupRepository extends JpaRepository<DiaryGroup, Long> {

    Optional<DiaryGroup> findByUserId(String userId);

    @Query("select dg from DiaryGroup dg where dg.isOuted = false and dg.diary = :diary")
    Optional<List<DiaryGroup>> findContainsUser(@Param("diary") Diary diary);

    Optional<DiaryGroup> findByDiaryAndUser(Diary diary, User user);

}
