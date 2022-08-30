package com.palette.diary.domain;

import com.palette.BaseEntity;
import com.palette.color.domain.Color;
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
@Table(name = "diary")
@Where(clause = "is_deleted = 0")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "diary_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE diary SET is_deleted = 1 WHERE id = ?")
public class Diary extends BaseEntity {

    @Column(length = 12)
    private String title;

    @Column(length = 10)
    private String invitationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeColor(Color color) {
        this.color = color;
    }

}
