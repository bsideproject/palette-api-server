package com.palette.diary.service;

import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DiaryServiceTest {

    @Test
    void test() {
        LocalDateTime now = LocalDateTime.of(
            2022, 9, 14, 22, 19, 25
        );
        Date triggerJobAt = java.sql.Timestamp.valueOf(
            now.minusHours(12L * 3));
        System.out.println(triggerJobAt);
    }

}