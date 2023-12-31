package com.spharos.ssgpoint.pointgift.application;

import com.spharos.ssgpoint.point.application.PointService;
import com.spharos.ssgpoint.point.domain.Point;
import com.spharos.ssgpoint.point.dto.PointCreateDto;
import com.spharos.ssgpoint.point.dto.PointFilterSumDto;
import com.spharos.ssgpoint.point.infrastructure.PointRepository;
import com.spharos.ssgpoint.point.vo.PointFilterVo;
import com.spharos.ssgpoint.pointgift.domain.PointGift;
import com.spharos.ssgpoint.pointgift.domain.PointGiftStatusType;
import com.spharos.ssgpoint.pointgift.domain.PointGiftType;
import com.spharos.ssgpoint.pointgift.dto.*;
import com.spharos.ssgpoint.pointgift.infrastructure.PointGiftRepository;
import com.spharos.ssgpoint.pointgift.vo.PointListInVo;
import com.spharos.ssgpoint.user.domain.User;
import com.spharos.ssgpoint.user.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointGiftServiceImpl implements PointGiftService {

    private final PointService pointService;

    private final UserRepository userRepository;
    private final PointGiftRepository pointGiftRepository;

    private final PointRepository pointRepository;


    // 포인트 선물 수신인 확인
    @Override
    public User getPointGiftUser(String phone, String name) {

        return userRepository.findByPhoneAndName(phone, name).orElseThrow(() ->
                new IllegalArgumentException("입력하신 정보로 가입된 신세계포인트 회원이 없습니다."
                        + System.lineSeparator()
                        + "포인트 선물하기는 신세계포인트 회원에게만 가능합니다."));

    }

    // 포인트 선물 보내기
    @Override
    public PointGiftIdDto createPointGift(String UUID, PointGiftCreateDto pointGiftCreateDto) {
        // 보낸 사람 포인트 테이블에 저장
        PointCreateDto pointCreateDto = PointCreateDto.builder()
                .point(pointGiftCreateDto.getPoint())
                .title(pointGiftCreateDto.getName() + "(ID : " + pointGiftCreateDto.getLoginId() + ")")
                .content("보낸 선물 : " + pointGiftCreateDto.getStatus())
                .statusType("1")
                .type("3")
                .user(UUID)
                .build();

        Point point = pointService.createPoint(UUID, pointCreateDto);


        PointGift save = pointGiftRepository.save(PointGift.builder()
                .point(pointGiftCreateDto.getPoint())
                .message(pointGiftCreateDto.getMessage())
                .type(PointGiftType.valueOf(pointGiftCreateDto.getType()))
                .status(PointGiftStatusType.valueOf(pointGiftCreateDto.getStatus()))
                .UUID(pointGiftCreateDto.getUUID())
                .loginId(pointGiftCreateDto.getLoginId())
                .name(pointGiftCreateDto.getName())
                .pointId(point.getId())
                .build());
        return PointGiftIdDto.builder().id(save.getId()).build();

    }

    // 포인트 선물 수락/거절
    @Transactional
    @Override
    public void updatePoint(Long id, String status) {
        // 포인트 선물 ID로 포인트 선물 정보 찾기
        Optional<PointGift> pointGiftList = pointGiftRepository.findById(id);


        // 보낸 사람 정보 찾기
        User sender = userRepository.findByUuid(pointGiftList.get().getUUID()).orElseThrow(() ->
                new IllegalArgumentException("보내는 사람 정보 없음"));

        // 받는 사람 정보 찾기
        User receiver = userRepository.findByLoginId(pointGiftList.get().getLoginId()).orElseThrow(() ->
                new IllegalArgumentException("받는 사람 정보 없음"));

        // 포인트 선물 수락 시
        if (status.equals("1")) {
            // 포인트 선물 상태 변경
            pointGiftList.get().update("수락");

            // 받는 사람 포인트 테이블에 저장
            PointCreateDto pointCreateDto = PointCreateDto.builder()
                    .point(pointGiftList.get().getPoint())
                    .title(sender.getName() + "(ID : " + sender.getLoginId() + ")")
                    .content("받은 선물 : " + pointGiftList.get().getStatus())
                    .statusType("0")
                    .type("3")
                    .user(receiver.getUuid())
                    .build();
            pointService.createPoint(receiver.getUuid(), pointCreateDto);

            //해야할것 보낸 선물 수락 저장이 안되있다 보낸선물: 대기 -> 보낸선물 수락으로 변경해야한다
            Long pointId = pointGiftList.get().getPointId();
            Point point = pointRepository.findById(pointId).orElseThrow(() ->
                    new IllegalArgumentException("보낸 선물 정보 없음"));
            log.info("pointGiftList.get().getStatus()={}",pointGiftList.get().getStatus());
            point.changeGiftStatus("보낸 선물 : " + pointGiftList.get().getStatus());

        }

        // 포인트 선물 거절 시
        if (status.equals("2")) {
            // 포인트 선물 상태 변경
            pointGiftList.get().update("거절");

            // 보낸 사람 포인트 테이블에 저장
            PointCreateDto pointCreateDto = PointCreateDto.builder()
                    .point(pointGiftList.get().getPoint())
                    .title(receiver.getName() + "(ID : " + receiver.getLoginId() + ")")
                    .content("보낸 선물 : " + pointGiftList.get().getStatus())
                    .statusType("2")
                    .type("3")
                    .user(sender.getUuid())
                    .build();
            pointService.createPoint(sender.getUuid(), pointCreateDto);

            //보낸선물: 대기 -> 거절
            Long pointId = pointGiftList.get().getPointId();
            Point point = pointRepository.findById(pointId).orElseThrow(() ->
                    new IllegalArgumentException("보낸 선물 정보 없음"));
            point.changeGiftStatus("보낸 선물 : " + pointGiftList.get().getStatus());

        }

    }

    // TODO: 안 쓰면 삭제
    // 포인트 선물 목록 (테스트 용)
    @Override
    public List<PointGiftGetDto> getPointGiftByUser(String UUID) {
        List<PointGift> pointGiftList = pointGiftRepository.findByUUID(UUID);

        return pointGiftList.stream().map(pointGift -> PointGiftGetDto.builder()
                .point(pointGift.getPoint())
                .message(pointGift.getMessage())
                .type(pointGift.getType().getValue())
                .status(pointGift.getStatus().getValue())
                .UUID(pointGift.getUUID())
                .loginId(pointGift.getLoginId())
                .name(pointGift.getName())
                .createdDate(LocalDate.from(pointGift.getCreatedDate()))
                .build()
        ).toList();
    }

    // 포인트 내역 - 선물
    @Override
    public Slice<PointGiftListDto> getPointGiftList(Long lastId, String UUID, Pageable page, LocalDate startDate, LocalDate endDate) {
        return pointGiftRepository.findPointGiftList(lastId, UUID,startDate, endDate, page);

    }


    // 포인트 내역 - 선물 적립 사용 합계
    @Override
    public PointFilterSumDto sumPointsGiftByFilter(String UUID, LocalDate startDate, LocalDate endDate) {
        return pointGiftRepository.sumPointsGiftByFilter(UUID, startDate, endDate);
    }

    //포인트 선물 클릭시 선물 왔는지 조회
    @Override
    public PointGiftCheckDto checkPointGift(String name) {
        Optional<PointGiftCheckDto> idByGift = pointGiftRepository.findIdByGift(name);
        if(!idByGift.isPresent()){
            return PointGiftCheckDto.builder().id(0L).build();
        }
        else{
            return idByGift.get();
        }
    }

    //포인트 선물- 전체 리스트
    @Override
    public Slice<PointGiftListDto> getMyPointGiftList(Long pointId, String uuid, Pageable page) {
        return pointGiftRepository.findMyPointGiftList(pointId,uuid, page);
    }


}
