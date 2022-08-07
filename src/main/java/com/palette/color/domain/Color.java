package com.palette.color.domain;

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
@Table(name = "color")
@Where(clause = "is_deleted = 0")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "color_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE color SET is_deleted = 1 WHERE id = ?")
public class Color extends BaseEntity {

    @Column(length = 7, unique = true, nullable = false)
    private String hexCode;

    @Column(nullable = false, name = "\"order\"") //예약어 사용
    private Integer order;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

}
