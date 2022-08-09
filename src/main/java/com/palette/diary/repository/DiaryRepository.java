package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    @Query("SELECT d FROM DiaryGroup g " +
            "RIGHT JOIN Diary d ON g.diary = d.id " +
            "WHERE g.user = :user"
    )
    public List<Diary> findUserDiaries(@Param("user") User user);

    Optional<Diary> findByInvitationCode(String invitationCode);
}
