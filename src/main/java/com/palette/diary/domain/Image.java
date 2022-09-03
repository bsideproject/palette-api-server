package com.palette.diary.domain;

import com.palette.BaseEntity;
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
@Table(name = "image")
@Where(clause = "is_deleted = 0")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "image_id")),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE history SET is_deleted = 1 WHERE id = ?")
public class Image extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false, name = "\"path\"") //예약어 사용
    private String path;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void setPage(Page page) {
        this.page = page;
    }

}
