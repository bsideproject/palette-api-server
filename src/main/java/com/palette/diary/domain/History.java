package com.palette.diary.domain;

import com.palette.BaseEntity;
import java.time.LocalDateTime;
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
@Table(name = "history")
@Where(clause = "is_deleted = 0")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "history_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE history SET is_deleted = 1 WHERE id = ?")
public class History extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    @Column(name = "is_deadlined", columnDefinition = "tinyint(1) default 0")
    private Boolean isDeadlined = false;

    @Builder.Default
    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    private Boolean isDeleted = false;

}
