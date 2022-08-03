package com.palette.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Getter
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ElementCollection(targetClass = SocialType.class)
    @CollectionTable(name = "social_type")
    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private Collection<SocialType> socialTypes;

    @Builder
    public User(Long id, String email, Collection<SocialType> socialTypes) {
        this.id = id;
        this.email = email;
        this.socialTypes = socialTypes;
    }

    public boolean addSocialType(SocialType socialType) {
        if (this.socialTypes.contains(socialType)) {
            return false;
        }
        this.socialTypes.add(socialType);
        return true;
    }
}
