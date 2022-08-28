package com.palette.token.domain;


import com.palette.BaseEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String tokenValue;

    @Column(nullable = false)
    private Date expiryDate;

    public RefreshToken(String email, String tokenValue, Date expiryDate) {
        this.email = email;
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
    }
}