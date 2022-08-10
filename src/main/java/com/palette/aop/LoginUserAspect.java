package com.palette.aop;

import com.palette.infra.jwtTokenProvider.JwtTokenProvider;
import com.palette.infra.jwtTokenProvider.JwtTokenType;
import com.palette.resolver.LoginUser;
import com.palette.utils.AuthorizationExtractor;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginUserAspect {

    private final JwtTokenProvider jwtTokenProvider;

    @Around("@annotation(com.palette.resolver.Authentication)")
    public Object test(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = AuthorizationExtractor.extract(request);

        Object[] args = pjp.getArgs();
        Object inputArgument = args[0];

        String email = jwtTokenProvider.getEmailFromPayLoad(token, JwtTokenType.ACCESS_TOKEN);
        LoginUser loginUser = new LoginUser(email);
        Object resultObj = pjp.proceed(new Object[]{inputArgument, loginUser});
        return resultObj;
    }

}
