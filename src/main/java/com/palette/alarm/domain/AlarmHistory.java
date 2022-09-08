package com.palette.alarm.domain;

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
@Table(name = "alarm_history")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "alarm_history_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String movePage;

    @Column(nullable = false)
    private Long diaryId;

    @Column(nullable = true)
    private Long historyId;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    public void alarmRead() {
        isRead = true;
    }

}
