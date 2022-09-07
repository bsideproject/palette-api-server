package com.palette.diary.repository.query;

import static com.palette.diary.domain.QDiary.diary;
import static com.palette.diary.domain.QDiaryGroup.diaryGroup;
import static com.palette.diary.domain.QHistory.history;

import com.palette.common.BaseRepository;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiaryQueryRepository extends BaseRepository {

    private final JPAQueryFactory queryFactory;

    public List<DiaryGroup> findByUser(User user, PageRequest pageRequest) {
        return queryFactory.selectFrom(diaryGroup)
            .where(
                condition(user, diaryGroup.user::eq),
                condition(false, diaryGroup.isOuted::eq)
            )
            .orderBy(diaryGroup.createdAt.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();
    }

    public List<History> findPastHistories(Diary diary) {
        return queryFactory.selectFrom(history)
            .where(
                condition(diary, history.diary::eq),
                condition(LocalDateTime.now(), history.endDate::lt)
            )
            .orderBy(history.createdAt.desc())
            .fetch();
    }

    public List<History> findHistories(User user, Diary paramDiary, PageRequest pageRequest) {
        return queryFactory.selectFrom(history)
            .join(diary).on(history.diary.id.eq(diary.id))
            .join(diaryGroup).on(diary.id.eq(diaryGroup.diary.id))
            .where(
                condition(user, diaryGroup.user::eq),
                condition(paramDiary, diaryGroup.diary::eq),
                condition(false, diaryGroup.isOuted::eq)
            )
            .orderBy(history.createdAt.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();
    }

}
