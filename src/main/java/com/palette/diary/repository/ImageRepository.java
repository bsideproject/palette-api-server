package com.palette.diary.repository;

import com.palette.diary.domain.Image;
import com.palette.diary.domain.Page;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByPage(Page page);
}
