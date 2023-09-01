package com.spharos.ssgpoint.associatepointcard.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssociatePointCardGetVo {

    private String number;
    private String type;

}
