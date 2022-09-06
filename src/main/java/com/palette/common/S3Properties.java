package com.palette.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Properties {

    public static String domain;

    @Value("${s3.domain}")
    public void setDomain(String domain) {
        this.domain = domain;
    }

}
