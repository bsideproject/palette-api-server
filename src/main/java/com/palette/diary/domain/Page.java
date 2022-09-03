package com.palette.diary.domain;

import com.palette.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@Table(name = "page")
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "page_id")),
})
@Where(clause = "is_deleted = 0")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE page SET is_deleted = 1 WHERE id = ?")
public class Page extends BaseEntity {

    @Builder.Default
    @OneToMany(mappedBy = "page", cascade = CascadeType.PERSIST)
    private List<Image> images = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    /**
     * 연관관계 편의 메소드
     */
    public void addImage(Image image) {
        this.images.add(image);
        //무한 루프 방지
        if (image.getPage() != this) {
            image.setPage(this);
        }
    }

}
