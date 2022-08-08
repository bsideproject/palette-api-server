package com.palette.diary.repository;

import com.palette.diary.domain.Diary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByInvitationCode(String invitationCode);
}
