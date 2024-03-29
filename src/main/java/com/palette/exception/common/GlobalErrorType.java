package com.palette.exception.common;

import com.netflix.graphql.types.errors.ErrorType;
import com.palette.exception.graphql.ColorNotFoundException;
import com.palette.exception.graphql.DiaryDiscardException;
import com.palette.exception.graphql.DiaryExistUserException;
import com.palette.exception.graphql.DiaryNotFoundException;
import com.palette.exception.graphql.DiaryOutedUserException;
import com.palette.exception.graphql.DiaryOverUserException;
import com.palette.exception.graphql.HistoryNotFoundException;
import com.palette.exception.graphql.InviteCodeNotFoundException;
import com.palette.exception.graphql.PageNotFoundException;
import com.palette.exception.graphql.PermissionDeniedException;
import com.palette.exception.graphql.ProgressedHistoryException;
import com.palette.exception.graphql.UserNotFoundExceptionForGraphQL;
import com.palette.exception.rest.DeletedUserException;
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
    A004("A004", "권한이 없는 사용자입니다.", ErrorType.PERMISSION_DENIED, PermissionDeniedException.class),
    A005("A005", "이미 탈퇴한 사용자입니다.", ErrorType.UNAVAILABLE, DeletedUserException.class),

    D001("D001", "초대코드가 존재하지 않습니다.", ErrorType.NOT_FOUND, InviteCodeNotFoundException.class),
    D002("D002", "일기가 존재하지 않습니다.", ErrorType.NOT_FOUND, DiaryNotFoundException.class),
    D003("D003", "사용할 수 없는 일기장입니다.", ErrorType.BAD_REQUEST, DiaryOverUserException.class),
    D004("D004", "해당 일기에 이미 가입되어 있습니다.", ErrorType.BAD_REQUEST, DiaryExistUserException.class),
    D005("D005", "이전에 나간적이 있는 그룹입니다.", ErrorType.BAD_REQUEST, DiaryOutedUserException.class),
    D006("D006", "진행중인 히스토리가 있습니다", ErrorType.BAD_REQUEST, ProgressedHistoryException.class),
    D007("D006", "히스토리가 존재하지 않습니다.", ErrorType.BAD_REQUEST, HistoryNotFoundException.class),
    D008("D008", "페이지가 존재하지 않습니다.", ErrorType.BAD_REQUEST, PageNotFoundException.class),
    D009("D008", "종료된 일기장 입니다.", ErrorType.BAD_REQUEST, DiaryDiscardException.class),

    U001("U001", "유저가 존재하지 않습니다.", ErrorType.NOT_FOUND, UserNotFoundExceptionForGraphQL.class),

    C001("C001", "색깔이 존재하지 않습니다.", ErrorType.NOT_FOUND, ColorNotFoundException.class),

    R001("R001", "알림내역이 존재하지 않습니다.", ErrorType.NOT_FOUND, ColorNotFoundException.class),

    F001("F001", "업로드에 실패하였습니다. 50MB 이하의 파일 사이즈를 등록해주세요.", ErrorType.BAD_REQUEST, null);

    private final String code;
    private final String message;
    private final ErrorType errorType;
    private final Class<? extends BaseException> classType;

}
