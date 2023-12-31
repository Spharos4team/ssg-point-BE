package com.spharos.ssgpoint.offlinepointcard.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfflinePointCardCreateDto {

    private String number;
    private Integer CVC;
    private String alliance;
    private String store;

}
