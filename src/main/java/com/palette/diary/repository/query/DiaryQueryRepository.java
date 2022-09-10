package com.palette.diary.repository.query;

import static com.palette.diary.domain.QDiary.diary;
import static com.palette.diary.domain.QDiaryGroup.diaryGroup;
import static com.palette.diary.domain.QHistory.history;
import static com.palette.diary.domain.QImage.image;
import static com.palette.diary.domain.QPage.page;
import static com.palette.user.domain.QUser.user;

import com.palette.common.BaseRepository;
import com.palette.diary.domain.Diary;
import com.palette.diary.domain.DiaryGroup;
import com.palette.diary.domain.History;
import com.palette.diary.domain.Page;
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

    public List<DiaryGroup> findByDiary(Diary paramDiary) {
        return queryFactory.selectFrom(diaryGroup)
            .join(diaryGroup.user, user).fetchJoin()
            .join(diaryGroup.diary, diary).fetchJoin()
            .where(
                condition(paramDiary, diaryGroup.diary::eq)
            )
            .fetch();
    }

    public History findProgressHistory(Diary diary) {
        LocalDateTime now = LocalDateTime.now();
        return queryFactory.selectFrom(history)
            .where(
                condition(diary, history.diary::eq),
                condition(now, history.startDate::loe),
                condition(now, history.endDate::goe)
            )
            .orderBy(history.createdAt.desc())
            .fetchOne();
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

    public List<Page> findPage(History history, PageRequest pageRequest) {
        return queryFactory.selectFrom(page)
            .join(page.images, image).fetchJoin()
            .where(
                condition(history, page.history::eq)
            )
            .orderBy(page.createdAt.desc())
            .offset(pageRequest.getOffset())
            .limit(pageRequest.getPageSize())
            .fetch();
    }

    public List<Page> findPage(History history) {
        return queryFactory.selectFrom(page)
            .join(page.images, image).fetchJoin()
            .where(
                condition(history, page.history::eq)
            )
            .orderBy(page.createdAt.desc())
            .fetch();
    }

}
