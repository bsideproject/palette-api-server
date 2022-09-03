package com.palette.diary.repository.query;

import static com.palette.diary.domain.QDiaryGroup.diaryGroup;

import com.palette.common.BaseRepository;
import com.palette.diary.domain.DiaryGroup;
import com.palette.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiaryQueryRepository extends BaseRepository {

    private final JPAQueryFactory queryFactory;

    public List<DiaryGroup> findByUser(User user) {
        return queryFactory.selectFrom(diaryGroup)
            .where(
                condition(user, diaryGroup.user::eq)
            )
            .orderBy(diaryGroup.createdAt.desc())
            .limit(3)
            .fetch();
    }

}
