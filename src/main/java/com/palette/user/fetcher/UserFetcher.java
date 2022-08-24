package com.palette.user.fetcher;

import com.netflix.graphql.dgs.*;
import com.palette.diary.domain.Diary;
import com.palette.diary.repository.DiaryGroupRepository;
import com.palette.diary.repository.DiaryRepository;
import com.palette.resolver.Authentication;
import com.palette.resolver.LoginUser;
import com.palette.user.domain.User;
import com.palette.user.fetcher.dto.AddFcmTokenInput;
import com.palette.user.fetcher.dto.DeleteFcmTokenInput;
import com.palette.user.fetcher.dto.EditMyProfileInput;
import com.palette.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
public class UserFetcher {
    private final UserRepository userRepository;
    private final DiaryGroupRepository diaryGroupRepository;
    private final DiaryRepository diaryRepository;
    private final EntityManager entityManager;

    @Authentication
    @DgsQuery(field = "myProfile")
    public User getMyProfile(@InputArgument LoginUser loginUser) {
        return userRepository.findByEmail(loginUser.getEmail()).orElseThrow(); // TODO: UserNotFoundException
    }

    @DgsData(parentType = "User", field = "diaries")
    public List<Diary> getUserDiaries(DgsDataFetchingEnvironment dfe) {
        User user = dfe.getSource();
        return diaryRepository.findUserDiaries(user);
    }

    @Authentication
    @DgsMutation
    public User editMyProfile(@InputArgument EditMyProfileInput editMyProfileInput, LoginUser loginUser) {
        Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException(); // TODO: UserNotFoundException
        }
        User user = userOptional.get();
        Boolean agreeWithTerms = editMyProfileInput.getAgreeWithTerms();
        String profileImg = editMyProfileInput.getProfileImg();
        String nickname = editMyProfileInput.getNickname();

        if (agreeWithTerms != null) user.setAgreeWithTerms(agreeWithTerms);
        if (profileImg != null) user.setProfileImg(profileImg);
        if (nickname != null) user.setNickname(nickname);
        userRepository.save(user);

        return user;
    }

    @Authentication
    @DgsMutation
    public User addFcmToken(@InputArgument AddFcmTokenInput addFcmTokenInput, LoginUser loginUser) {
        Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException(); // TODO: UserNotFoundException
        }
        User user = userOptional.get();
        user.addFcmToken(addFcmTokenInput.getToken());
        userRepository.save(user);
        return user;
    }

    @Authentication
    @DgsMutation
    public Boolean deleteFcmToken(@InputArgument DeleteFcmTokenInput deleteFcmTokenInput, LoginUser loginUser) {
        Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException(); // TODO: UserNotFoundException
        }
        User user = userOptional.get();
        Boolean isRemoved = user.deleteFcmToken(deleteFcmTokenInput.getToken());
        if (isRemoved) {
            userRepository.save(user);
        }
        return isRemoved;
    }
}
