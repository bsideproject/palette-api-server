package com.palette.user.domain;

import com.palette.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "user_id")),
})
@SQLDelete(sql = "UPDATE user SET is_deleted = 1 WHERE user_id = ?")
//@Where(clause = "is_deleted = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @ElementCollection(targetClass = SocialType.class)
    @CollectionTable(name = "social_type")
    @Column(name = "social_type")
    @Enumerated(EnumType.STRING)
    private Set<SocialType> socialTypes = new HashSet<>();

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
    @Column(name = "fcm_token")
    private Set<String> fcmTokens = new HashSet<>();

    @Builder.Default
    @Column(name = "push_enabled")
    private Boolean pushEnabled = false;

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
