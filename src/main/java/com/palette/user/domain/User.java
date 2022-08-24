package com.palette.user.domain;

import com.palette.BaseEntity;
import com.palette.diary.domain.DiaryGroup;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(targetClass = SocialType.class)
    @CollectionTable(name = "social_type")
    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private Collection<SocialType> socialTypes;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "profile_img", nullable = true)
    private String profileImg = null;

    @Builder.Default
    @Column(name = "agree_with_terms", nullable = false)
    private Boolean agreeWithTerms = false;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "fcm_tokens")
    @Column(name = "fcm_tokens")
    private Collection<String> fcmTokens = new ArrayList<>();

    public boolean addSocialType(SocialType socialType) {
        if (this.socialTypes.contains(socialType)) {
            return false;
        }
        this.socialTypes.add(socialType);
        return true;
    }

    public void addFcmToken(String token) {
        this.fcmTokens.add(token);
    }

    public Boolean deleteFcmToken(String token) {
        return this.fcmTokens.remove(token);
    }
}
