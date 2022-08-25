package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, Long> {

    //TODO: 하나만 조회되게끔 히스토리 등록시 기존에 존재하는 히스토리가 있는지 유효성 검사 필요
    @Query("select h from History h where h.isDeadlined = false and h.diary = :diary order by h.createdAt desc ")
    History findProgressHistory(@Param("diary") Diary diary);
}
