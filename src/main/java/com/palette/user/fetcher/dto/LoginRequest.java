package com.palette.user.fetcher.dto;

import com.palette.user.domain.SocialType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "The email is required.")
    @Email(message = "The email is invalid.", flags = {Pattern.Flag.CASE_INSENSITIVE})
    private String email;

    @NotNull(message = "The social type is required.")
    private String socialType;
}