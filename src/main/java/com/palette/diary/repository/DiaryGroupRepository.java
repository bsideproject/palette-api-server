package com.palette.diary.repository;

import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DiaryGroupRepository extends JpaRepository<DiaryGroup, Long> {
    Optional<DiaryGroup> findByUserId(String userId);

}
