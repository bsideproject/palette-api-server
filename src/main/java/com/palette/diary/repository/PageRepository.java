package com.palette.diary.repository;

import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long> {

    List<Page> findByHistory(History history);
}
