package com.palette.diary.domain;

import com.palette.BaseEntity;
import com.palette.user.domain.User;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary_group")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "group_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryGroup extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @Builder.Default
    @Column(name = "is_outed", columnDefinition = "tinyint(1) default 0")
    private Boolean isOuted = false;

    @Column(name = "is_admin", columnDefinition = "tinyint(1) default 0")
    private Boolean isAdmin;

}
