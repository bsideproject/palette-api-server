package com.palette.diary.domain;

import com.palette.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@AllArgsConstructor
@Table(name = "diary")
@Where(clause = "isDel = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE diary SET isDel = 1 WHERE id = ?")
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @Column(length = 12)
    private String title;

    @Column(length = 10)
    private String invitationCode;

    @Column(length = 7)
    private String color;

    @Builder.Default
    @ColumnDefault("0")
    @Column(name = "del_status")
    private Integer isDel = 0;

}
