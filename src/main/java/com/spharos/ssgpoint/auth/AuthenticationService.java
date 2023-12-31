package com.spharos.ssgpoint.auth;

import com.spharos.ssgpoint.auth.vo.AuthenticationRequest;
import com.spharos.ssgpoint.auth.vo.AuthenticationResponse;
import com.spharos.ssgpoint.config.security.JwtTokenProvider;
import com.spharos.ssgpoint.exception.CustomException;
import com.spharos.ssgpoint.point.domain.Point;
import com.spharos.ssgpoint.pointcard.domain.PointCard;
import com.spharos.ssgpoint.pointcard.domain.PointCardType;
import com.spharos.ssgpoint.pointcard.domain.PointCardTypeConverter;
import com.spharos.ssgpoint.pointcard.dto.PointCardCreateDto;
import com.spharos.ssgpoint.pointcard.infrastructure.PointCardRepository;
import com.spharos.ssgpoint.term.domain.UserTermList;

import com.spharos.ssgpoint.user.domain.Role;
import com.spharos.ssgpoint.user.domain.User;
import com.spharos.ssgpoint.user.dto.user.UserSignUpDto;
import com.spharos.ssgpoint.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final RedisTemplate redisTemplate;
    private final PointCardRepository  pointCardRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${JWT.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Transactional
    public AuthenticationResponse signup(UserSignUpDto userSignUpDto) {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        log.info("userSignUpDto name={}", userSignUpDto.getName() );

        Map<String, Boolean> term = userSignUpDto.getTerm();
        UserTermList userTermList = new UserTermList();
        UserTermList termList = userTermList.builder().termJson(term).build();

        User user = User.builder()
                .loginId(userSignUpDto.getLoginId())
                .uuid(uuidString)
                .name(userSignUpDto.getName())
                .password(userSignUpDto.getPassword())
                .email(userSignUpDto.getEmail())
                .phone(userSignUpDto.getPhone())
                .address(userSignUpDto.getAddress())
                .term(termList)
                .status(1)
                .role(Role.USER)
                .build();

        String jwtToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        user.hashPassword(user.getPassword());
        User saveUser = userRepository.save(user);


        //생성후 바코드 저장
        String pointBardCode1 = createPointBardCode(user);
        String validateBarcode1 = validateBarcode(pointBardCode1);
        String pointBardCode2 = createPointBardCode(user);
        String validateBarcode2 = validateBarcode(pointBardCode2);

        //회원가입후 포인트카드  무조건 2개 생성
        PointCardType pointCardType
                = new PointCardTypeConverter().convertToEntityAttribute("ON");
        PointCard build = PointCard.builder()
                .agency("신세계")
                .UUID(uuidString)
                .number(validateBarcode1)
                .type(pointCardType)
                .build();
        pointCardRepository.save(build);

        PointCard build1 = PointCard.builder()
                .agency("신세계")
                .UUID(uuidString)
                .number(validateBarcode2)
                .type(pointCardType)
                .build();

        pointCardRepository.save(build);
        pointCardRepository.save(build1);


        return AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 전체 바코드 숫자 만들기 9350+랜덤3자리+uuid9자리
     */
    private String createPointBardCode(User user) {

        String generateBarcode = generateBarcode();
        String formattedId;

        if (user.getId() > 999999999) {
            String s = String.valueOf(user.getId());
            String substring = s.substring(1, s.length());
            formattedId = substring;
        } else {
            formattedId = String.format("%09d", user.getId());
        }
        return generateBarcode+formattedId;
    }

    /**
     * 바코드 앞에 7자리
     */
    private String generateBarcode() {
        String fixNumber = "9350";
        Random random = new Random();
        String randomDigits = String.format("%03d", random.nextInt(1000));
        return fixNumber + randomDigits;
    }

    /**
     * 바코드만들고 만든거 중복 검사 , todo:추가 수정 필요함
     */
    private String validateBarcode(String checkBarcode) {
        Optional<PointCard> byBarCode = pointCardRepository.findByNumber(checkBarcode);
        if (byBarCode.isPresent()){
            //중복인경우
            String substring = checkBarcode.substring(4, 7);
            int plus = Integer.parseInt(substring) + 1;

            String barcode = checkBarcode.substring(0, 4) + String.format("%03d", plus) + checkBarcode.substring(7);
            return validateBarcode(barcode); // 중복된 경우 재귀 호출하여 검사
            //return barcode;
        }
        else{
            return checkBarcode;
        }
    }


    /**
     * 로그인 인증
     * @param authenticationRequest json 요청
     * @param
     * @return
     */

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getLoginId(),
                        authenticationRequest.getPassword()
                )
        );

        User user = userRepository.findByLoginId(authenticationRequest.getLoginId()).orElseThrow(()-> new IllegalArgumentException("아이디가 존재하지 않습니다."));
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("탈퇴한 회원입니다.");
        }
        log.info("user is : {}" , user.getUuid());
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        String uuid = jwtTokenProvider.getUUID(accessToken);

        Optional<Point> totalByUuid = userRepository.findTotalByUuid(uuid);

        String barCode = pointCardRepository.findNumberByUUID(uuid); //바코드 가져오기(바코드는 유니크
        int totalPoint = (totalByUuid.isPresent()) ? totalByUuid.get().getTotalPoint() : 0;

        log.info("accessToken is : {}" , accessToken);
        log.info("refreshToken is : {}" , refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthenticationResponse.User.builder()
                        .name(user.getName())
                        .point(totalPoint)
                        .uuid(user.getUuid())
                        .bardCode(barCode)
                        .build())
                .build();
    }


    public AuthenticationResponse refreshToken(String refreshToken) {

        final String UUID;
        UUID = jwtTokenProvider.getUUID(refreshToken);

            User user = this.userRepository.findByUuid(UUID)
                    .orElseThrow();
             // refresh 토큰 검증해서 만료 안되었으면

        String redisInRefreshToken = (String) redisTemplate.opsForValue().get(UUID); //레디스에서 key uuid로 value refreshToken 가져온다
        if(!redisInRefreshToken.equals(refreshToken)){    //내가 가진 refreshtoken이랑 레디스 refreshtoken 다르면 예외
            throw new CustomException("Refresh Token doesn't match."); // Redis에서 해당 유저 UUIO key 를 삭제하고 재로그인 하도록 클라이언트를 리턴한다
            //redisTemplate.delete(uuid);  = 토큰삭제
        }

                //내가 가진 refreshtoken이랑 레디스 refreshtoken같으면 레디스 안에 수정
        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        redisTemplate.opsForValue().set(
                UUID,
                newRefreshToken,
                refreshExpirationTime,
                TimeUnit.MILLISECONDS
        );
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .uuid(UUID)
                .build();


    }


    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String refreshToken) {
        //Token에서 로그인한 사용자 정보 get해 로그아웃 처리
        String uuid = jwtTokenProvider.getUUID(refreshToken);
        log.info("u is : {}" , uuid);
        redisTemplate.delete(uuid); //Token 삭제

    }

}
