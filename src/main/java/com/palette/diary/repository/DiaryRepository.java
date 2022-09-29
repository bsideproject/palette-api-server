package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import com.palette.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query("SELECT d FROM DiaryGroup g " +
        "RIGHT JOIN Diary d ON g.diary = d.id " +
        "WHERE g.user = :user AND g.isOuted = false"
    )
    public List<Diary> findUserDiaries(@Param("user") User user);

    Optional<Diary> findByInvitationCode(String invitationCode);

    int countByInvitationCode(String invitationCode);

}
