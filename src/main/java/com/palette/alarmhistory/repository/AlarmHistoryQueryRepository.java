package com.palette.alarmhistory.repository;

import static com.palette.alarmhistory.domain.QAlarmHistory.alarmHistory;

import com.palette.alarmhistory.domain.AlarmHistory;
import com.palette.common.BaseRepository;
import com.palette.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlarmHistoryQueryRepository extends BaseRepository {

    private final JPAQueryFactory queryFactory;

    public List<AlarmHistory> findAlarmHistories(User user, PageRequest pageRequest) {
        return queryFactory.selectFrom(alarmHistory)
            .where(
                condition(user, alarmHistory.user::eq),
                condition(LocalDateTime.now().minusDays(60L), alarmHistory.createdAt::goe)
            )
            .orderBy(alarmHistory.createdAt.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();
    }

}
