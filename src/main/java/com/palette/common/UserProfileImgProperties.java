package com.palette.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserProfileImgProperties {

    public static String defaultProfileImg;

    @Value("${default.profile.image.url}")
    public void setDomain(String defaultProfileImg) {
        this.defaultProfileImg = defaultProfileImg;
    }

}
