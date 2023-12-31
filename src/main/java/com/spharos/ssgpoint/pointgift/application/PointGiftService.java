package com.spharos.ssgpoint.pointgift.application;

import com.spharos.ssgpoint.point.dto.PointFilterSumDto;
import com.spharos.ssgpoint.point.vo.PointFilterVo;
import com.spharos.ssgpoint.pointgift.dto.*;
import com.spharos.ssgpoint.pointgift.vo.PointListInVo;
import com.spharos.ssgpoint.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;

public interface PointGiftService {

    // 포인트 선물 수신인 확인
    User getPointGiftUser(String phone, String name);

    // 포인트 선물 보내기
    PointGiftIdDto createPointGift(String UUID, PointGiftCreateDto pointGiftCreateDto);

    // 포인트 선물 수락/거절
    void updatePoint(Long id, String status);

    // TODO: 안 쓰면 삭제
    // 포인트 선물 목록 (테스트 용)
    List<PointGiftGetDto> getPointGiftByUser(String UUID);

    //포인트 내역 선물
    Slice<PointGiftListDto> getPointGiftList(Long pointId, String uuid, Pageable page, LocalDate startDate, LocalDate endDate);
    //포인트 선물 -전부
    Slice<PointGiftListDto> getMyPointGiftList(Long pointId, String uuid, Pageable page);


    PointFilterSumDto sumPointsGiftByFilter(String UUID, LocalDate startDate, LocalDate endDate);
    PointGiftCheckDto checkPointGift(String name);

}
