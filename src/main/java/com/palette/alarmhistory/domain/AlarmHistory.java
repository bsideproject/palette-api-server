package com.palette.alarmhistory.domain;

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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarm_history")
@Where(clause = "is_deleted = 0")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "alarm_history_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE alarm_history SET is_deleted = 1 WHERE id = ?")
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

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void read() {
        isRead = true;
    }

}
