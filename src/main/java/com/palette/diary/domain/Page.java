package com.palette.diary.domain;

import com.palette.BaseEntity;
import com.palette.user.domain.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Getter
@Builder
@Table(name = "page")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "page_id")),
})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Page extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Builder.Default
    @Column(nullable = false)
    private ArrayList<String> images = new ArrayList<>();

}
