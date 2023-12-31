package com.spharos.ssgpoint.pointgift.presentation;

import com.spharos.ssgpoint.point.dto.PointFilterSumDto;
import com.spharos.ssgpoint.point.vo.PointFilterSumVo;
import com.spharos.ssgpoint.pointgift.application.PointGiftService;
import com.spharos.ssgpoint.pointgift.dto.*;
import com.spharos.ssgpoint.pointgift.vo.PointGiftCreateVo;
import com.spharos.ssgpoint.pointgift.vo.PointListInVo;

import com.spharos.ssgpoint.pointgift.vo.PointListOutVo;
import com.spharos.ssgpoint.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class PointGiftController {

    private final PointGiftService pointGiftService;

    // 포인트 선물 수신인 확인
    @GetMapping("/point/gift/user")
    public ResponseEntity<PointGiftUserGetDto> getPointGiftUser(@RequestParam("phone") String phone, @RequestParam("name") String name) {
        User user = pointGiftService.getPointGiftUser(phone, name);

        PointGiftUserGetDto pointGiftUserGetDto = PointGiftUserGetDto.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();

        return ResponseEntity.ok(pointGiftUserGetDto);
    }

    // 포인트 선물 보내기
    @PostMapping("/point/gift")
    public ResponseEntity<PointGiftIdDto> createPointGift(@RequestParam("UUID") String UUID, @RequestBody PointGiftCreateVo pointGiftCreateVo) {
        PointGiftCreateDto pointGiftCreateDto = PointGiftCreateDto.builder()
                .point(pointGiftCreateVo.getPoint())
                .message(pointGiftCreateVo.getMessage())
                .type("선물사용")
                .status("대기")
                .UUID(UUID)
                .loginId(pointGiftCreateVo.getLoginId())
                .name(pointGiftCreateVo.getName())
                .build();

        return ResponseEntity.ok(pointGiftService.createPointGift(UUID, pointGiftCreateDto));

    }

    // 포인트 선물 수락/거절
    @PutMapping("/point/gift")
    public ResponseEntity<String> updatePoint(@RequestParam("id") Long id, @RequestParam("status") String status) {
        pointGiftService.updatePoint(id, status);

        return ResponseEntity.ok("포인트 선물 상태 변경 완료");
    }


    //포인트 내역 ->선물 목록
    @GetMapping("/point/gift-list")
    public ResponseEntity<Slice<PointListOutVo>> pointListFilter(@RequestParam("UUID") String UUID,
                                                                 @RequestParam(value = "lastId", required = false) Long lastId,
                                                                 @PageableDefault(size=10, sort="createdDate") Pageable pageRequest
            , @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                 @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){

        ModelMapper modelMapper = new ModelMapper();

        Slice<PointListOutVo> pointFilterOutVos = modelMapper.map(pointGiftService.getPointGiftList(lastId, UUID, pageRequest, startDate, endDate)
                , new TypeToken<Slice<PointListOutVo>>() {}.getType());
        return ResponseEntity.ok(pointFilterOutVos);

    }

    //포인트내역 -> 선물 목록 적립 사용 금액
    @GetMapping("/point/gift-sum")
    public ResponseEntity<PointFilterSumVo> giftPointListSum(@RequestParam("UUID") String UUID,
                                                             @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                             @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){

        PointFilterSumDto pointFilterSumDto = pointGiftService.sumPointsGiftByFilter(UUID, startDate,endDate);
        ModelMapper modelMapper = new ModelMapper();
        return ResponseEntity.ok(modelMapper.map(pointFilterSumDto, PointFilterSumVo.class));
    }

    //포인트 선물 -> 전체 날짜
    @GetMapping("/mypoint/gift-list")
    public ResponseEntity<Slice<PointListOutVo>> myPointListFilter(@RequestParam("UUID") String UUID,
                                                                   @RequestParam(value = "lastId", required = false) Long lastId,
                                                                   @PageableDefault(size=10, sort="createdDate") Pageable pageRequest){

        ModelMapper modelMapper = new ModelMapper();
        Slice<PointListOutVo> pointFilterOutVos = modelMapper.map(pointGiftService.getMyPointGiftList(lastId, UUID, pageRequest)
                , new TypeToken<Slice<PointListOutVo>>() {}.getType());
        return ResponseEntity.ok(pointFilterOutVos);

    }


    //포인트 선물 페이지 조회시 선물왔는지 확인
    //선물 없으면 무조건 id=0 있으면 해당하는 선물id 반환
    @GetMapping("/point/gift-check")
    public ResponseEntity<PointGiftCheckDto> checkPointGift(@RequestParam("name") String name){
        return ResponseEntity.ok(pointGiftService.checkPointGift(name));
    }
}
