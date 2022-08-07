package com.palette.diary.domain;

import com.palette.BaseEntity;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "diary")
@Where(clause = "isDeleted = 0")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "diary_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE diary SET isDeleted = 1 WHERE id = ?")
public class Diary extends BaseEntity {

    @Column(length = 12)
    private String title;

    @Column(length = 10)
    private String invitationCode;

    @Column(length = 7)
    private String color;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

}
