package com.palette.exception.common;

import com.netflix.graphql.types.errors.ErrorType;
import com.palette.exception.graphql.ColorNotFoundException;
import com.palette.exception.graphql.DiaryExistUserException;
import com.palette.exception.graphql.DiaryNotFoundException;
import com.palette.exception.graphql.DiaryOutedUserException;
import com.palette.exception.graphql.DiaryOverUserException;
import com.palette.exception.graphql.DummyException;
import com.palette.exception.graphql.InviteCodeNotFoundException;
import com.palette.exception.graphql.ProgressedHistoryException;
import com.palette.exception.graphql.UserNotFoundException;
import com.palette.exception.rest.SocialTypeNotFoundException;
import com.palette.exception.rest.TokenExpirationException;
import com.palette.exception.rest.TokenNotValidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorType {
    A001("A001", "토큰이 유효하지 않습니다.", null, TokenNotValidException.class),
    A002("A002", "만료된 토큰입니다.", null, TokenExpirationException.class),
    A003("A003", "존재하지 않는 소셜 로그인 방식입니다.", null, SocialTypeNotFoundException.class),
    A004("test", "test", ErrorType.BAD_REQUEST, DummyException.class),

    D001("D001", "초대코드가 존재하지 않습니다.", ErrorType.NOT_FOUND, InviteCodeNotFoundException.class),
    D002("D002", "일기가 존재하지 않습니다.", ErrorType.NOT_FOUND, DiaryNotFoundException.class),
    D003("D003", "인원이 초과되었습니다.", ErrorType.BAD_REQUEST, DiaryOverUserException.class),
    D004("D004", "해당 일기에 이미 가입되어 있습니다.", ErrorType.BAD_REQUEST, DiaryExistUserException.class),
    D005("D005", "이전에 나간적이 있는 그룹입니다.", ErrorType.BAD_REQUEST, DiaryOutedUserException.class),
    D006("D006", "진행중인 히스토리가 있습니다", ErrorType.BAD_REQUEST, ProgressedHistoryException.class),

    U001("U001", "유저가 존재하지 않습니다.", ErrorType.NOT_FOUND, UserNotFoundException.class),

    C001("C001", "색깔이 존재하지 않습니다.", ErrorType.NOT_FOUND, ColorNotFoundException.class);

    private final String code;
    private final String message;
    private final ErrorType errorType;
    private final Class<? extends BaseException> classType;

}

