package com.palette.filter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionFilter implements Filter {

    public static final String TRANSACTION_ID = "TRANSACTION-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        log.info("dsasadasd");
        HttpServletRequest req = (HttpServletRequest) request;
        String transactionId = Optional.ofNullable(req.getHeader(TRANSACTION_ID))
            .orElse(UUID.randomUUID().toString());

        MDC.put(TRANSACTION_ID, transactionId);

        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader(TRANSACTION_ID, transactionId);

        chain.doFilter(req, res);
    }

}
