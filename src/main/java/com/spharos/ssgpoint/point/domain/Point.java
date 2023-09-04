package com.spharos.ssgpoint.point.domain;

import com.spharos.ssgpoint.global.BaseEntity;
import com.spharos.ssgpoint.pointcard.domain.PointCard;
import com.spharos.ssgpoint.receipt.domain.Receipt;
import com.spharos.ssgpoint.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer totalPoint;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer point;

    @Column(nullable = false)
    private String title;

    private String content;

    private Integer used;

    @Column(nullable = false)
    @Convert(converter = PointTypeConverter.class)
    private PointType type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PointCard pointCard;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Receipt receipt;

}