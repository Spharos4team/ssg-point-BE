package com.spharos.ssgpoint.point.vo;

import com.spharos.ssgpoint.auth.vo.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointCreateVo {

    private Integer totalPoint;
    private Integer point;
    private String title;
    private String content;
    private String type;
    private Long pointCardId;

    private ReceiptDto receipt;

    @Data
    public static class ReceiptDto {
        private String alliance;
        private String brand;
        private String storeName;
        private String number;
        private Integer amount;
        private Integer point;
        private String cardName;
        private String cardNumber;

    }
}
