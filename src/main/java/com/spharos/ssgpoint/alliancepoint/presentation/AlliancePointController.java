package com.spharos.ssgpoint.alliancepoint.presentation;

import com.spharos.ssgpoint.alliancepoint.application.AlliancePointService;

import com.spharos.ssgpoint.alliancepoint.dto.AlliancePointCreateDto;
import com.spharos.ssgpoint.alliancepoint.dto.AlliancePointGetDto;
import com.spharos.ssgpoint.alliancepoint.dto.AlliancePointListDto;
import com.spharos.ssgpoint.alliancepoint.dto.AlliancePointUpdateDto;
import com.spharos.ssgpoint.alliancepoint.vo.AlliancePointCreateVo;
import com.spharos.ssgpoint.alliancepoint.vo.AlliancePointGetVo;
import com.spharos.ssgpoint.alliancepoint.vo.AlliancePointUpdateVo;

import com.spharos.ssgpoint.point.dto.PointFilterSumDto;
import com.spharos.ssgpoint.point.vo.PointFilterSumVo;
import com.spharos.ssgpoint.pointgift.vo.PointListInVo;
import com.spharos.ssgpoint.pointgift.vo.PointListOutVo;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AlliancePointController {

    private final AlliancePointService alliancePointService;

    // 제휴사 포인트 생성 (테스트 위해 생성)
    @PostMapping("/point/alliance")
    public ResponseEntity<String> createAlliancePoint(@RequestParam("UUID") String UUID, @RequestBody AlliancePointCreateVo alliancePointCreateVo) {

        AlliancePointCreateDto alliancePointCreateDto = AlliancePointCreateDto.builder()
                .type(alliancePointCreateVo.getType())
                .point(alliancePointCreateVo.getPoint())
                .build();
        alliancePointService.createAlliancePoint(UUID, alliancePointCreateDto);
        return ResponseEntity.ok("포인트생성");
    }

    // 제휴사 포인트 조회
    @GetMapping("/point/alliance")
    public List<AlliancePointGetVo> getAlliancePointByUUID(@RequestParam("UUID") String UUID) {
        List<AlliancePointGetDto> alliancePointGetDtoList
                = alliancePointService.getAlliancePointByUUID(UUID);

        return alliancePointGetDtoList.stream().map(alliancePointAllGetDto ->
                AlliancePointGetVo.builder()
                        .point(alliancePointAllGetDto.getPoint())
                        .type(alliancePointAllGetDto.getType())
                        .build()
        ).toList();
    }

    // 제휴사 포인트 전환
    @PutMapping("/point/alliance")
    public ResponseEntity<String> updateAlliancePoint(@RequestParam("UUID") String UUID, @RequestParam("type") String type,
                                    @RequestParam("status") String status,
                                    @RequestBody AlliancePointUpdateVo alliancePointUpdateVo) {
        AlliancePointUpdateDto alliancePointUpdateDto = AlliancePointUpdateDto.builder()
                .point(alliancePointUpdateVo.getPoint())
                .build();

        alliancePointService.updateAlliancePoint(UUID, type, status, alliancePointUpdateDto);
        return ResponseEntity.ok("포인트전환 완료");
    }

    // 전환 목록
    @GetMapping("/point/alliance/list")
    public ResponseEntity<Slice<PointListOutVo>> alliancePointListFilter
    (@RequestParam("UUID") String UUID,
     @RequestParam(value = "lastId", required = false) Long lastId,
     @PageableDefault(size = 10, sort = "createdDate") Pageable pageRequest,
     @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
     @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate)
    {
        ModelMapper modelMapper = new ModelMapper();
        //Slice<AlliancePointListDto> pointAllianceList = alliancePointService.getPointAllianceList(lastId, UUID, pageRequest, startDate,endDate);
        Slice<PointListOutVo> pointFilterOutVos = modelMapper.map(alliancePointService.getPointAllianceList(lastId,
                        UUID, pageRequest, startDate,endDate)
                , new TypeToken<Slice<PointListOutVo>>() {}.getType());

        // ResponseEntity로 감싸서 반환
       return ResponseEntity.ok(pointFilterOutVos);
        //return pointAllianceList;
    }

    // 전환 포인트 목록 적립 사용 합계
    @GetMapping("/point/alliance/sum")
    public ResponseEntity<PointFilterSumVo> giftPointListSum(@RequestParam("UUID") String UUID,
                                                             @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                             @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){

        PointFilterSumDto pointFilterSumDto = alliancePointService.sumPointsAllianceByFilter(UUID, startDate,endDate);
        ModelMapper modelMapper = new ModelMapper();
        return ResponseEntity.ok(modelMapper.map(pointFilterSumDto, PointFilterSumVo.class));


    }

}
