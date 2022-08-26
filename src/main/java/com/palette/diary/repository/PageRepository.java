package com.palette.diary.repository;

import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findByHistory(History history);
}
