package com.palette.alarmhistory.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.palette.alarmhistory.domain.AlarmHistory;
import com.palette.alarmhistory.fetcher.dto.AlarmHistoryPageInput;
import com.palette.alarmhistory.fetcher.dto.ReadAlarmHistoriesInput;
import com.palette.alarmhistory.repository.AlarmHistoryQueryRepository;
import com.palette.alarmhistory.repository.AlarmHistoryRepository;
import com.palette.exception.graphql.AlarmHistoryNotFoundException;
import com.palette.exception.graphql.UserNotFoundExceptionForGraphQL;
import com.palette.resolver.Authentication;
import com.palette.resolver.LoginUser;
import com.palette.user.domain.User;
import com.palette.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DgsComponent
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmHistoryFetcher {

    private final AlarmHistoryQueryRepository queryRepository;
    private final AlarmHistoryRepository repository;
    private final UserRepository userRepository;

    /**
     * GlobalErrorType 참고
     *
     * @throws UserNotFoundExceptionForGraphQL
     */
    @Authentication
    @DgsQuery(field = "alarmHistories")
    public List<AlarmHistory> findAlarmHistories(
        @InputArgument AlarmHistoryPageInput alarmHistoryPageInput,
        LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
            .orElseThrow(UserNotFoundExceptionForGraphQL::new);

        int offset = alarmHistoryPageInput.getOffset();
        int size = alarmHistoryPageInput.getSize();

        return queryRepository.findAlarmHistories(user, PageRequest.of(offset, size));
    }

    /**
     * GlobalErrorType 참고
     *
     * @throws AlarmHistoryNotFoundException
     */
    @DgsMutation
    @Transactional
    public Boolean readAlarmHistories(ReadAlarmHistoriesInput readAlarmHistoriesInput) {
        int count = repository.readAlarmHistories(
            readAlarmHistoriesInput.getAlarmHistoryIds());

        return true;
    }

}
