package com.palette.token.domain;


import com.palette.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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