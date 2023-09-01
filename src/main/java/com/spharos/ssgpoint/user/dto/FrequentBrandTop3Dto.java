package com.spharos.ssgpoint.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FrequentBrandTop3Dto {
    private String alliance; // 브랜드 이름
    private Long totalCount;  // 브랜드의 총 개수
}
