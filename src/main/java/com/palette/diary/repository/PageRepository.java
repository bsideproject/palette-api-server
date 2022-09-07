package com.palette.diary.repository;

import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {

    org.springframework.data.domain.Page<Page> findByHistory(History history, Pageable pageable);
}
