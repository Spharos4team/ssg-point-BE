package com.spharos.ssgpoint.event.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventOut {

    @NotNull(message = "이벤트 ID는 비워둘 수 없습니다.")
    private Long eventId;

    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Size(max = 50, message = "제목은 50자를 초과할 수 없습니다.")
    private String title;

    private String content;

    @NotBlank(message = "이벤트 유형은 비워둘 수 없습니다.")
    private String eventType;

    @NotBlank(message = "썸네일 URL은 비워둘 수 없습니다.")
    private String thumbnailUrl;

    private List<@NotBlank(message = "이벤트 이미지 URL은 비워둘 수 없습니다.") String> eventImages;

    @NotNull(message = "등록 날짜는 비워둘 수 없습니다.")
    private Date regDate;

    @NotNull(message = "시작 날짜는 비워둘 수 없습니다.")
    private Date startDate;

    @NotNull(message = "종료 날짜는 비워둘 수 없습니다.")
    private Date endDate;
}