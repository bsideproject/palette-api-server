package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.diary.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("select h from History h where h.isDeadlined = false and h.diary = :diary order by h.createdAt desc")
    History findProgressHistory(@Param("diary") Diary diary);
}
