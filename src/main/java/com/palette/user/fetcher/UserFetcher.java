package com.palette.user.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.palette.diary.domain.Diary;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DgsComponent
@RequiredArgsConstructor
public class UserFetcher {
    private final UserRepository userRepository;
    private final DiaryGroupRepository diaryGroupRepository;
    private final DiaryRepository diaryRepository;
    private final EntityManager entityManager;

    @DgsQuery(field = "myProfile")
    private User getMyProfile() { // TODO: AuthUser의 email 받아오기
        return userRepository.findByEmail("test@gmail.com").orElseThrow(); // TODO: UserNotFoundException
    }

    @DgsData(parentType = "User", field = "diaries")
    private List<Diary> getUserDiaries(DgsDataFetchingEnvironment dfe) {
        User user = dfe.getSource();
        return diaryRepository.findUserDiaries(user);
    }
}
