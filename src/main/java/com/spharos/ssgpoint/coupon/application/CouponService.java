package com.spharos.ssgpoint.coupon.application;

import com.spharos.ssgpoint.coupon.domain.Coupon;
import com.spharos.ssgpoint.coupon.domain.UserCoupon;
import com.spharos.ssgpoint.coupon.infrastructure.CouponRepository;
import com.spharos.ssgpoint.coupon.infrastructure.UserCouponRepository;
import com.spharos.ssgpoint.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    public List<Coupon> getAllAvailableCoupons() {
        // TODO: Define criteria for 'available' and modify this method accordingly
        return couponRepository.findAll();
    }

    public UserCoupon assignCouponToUser(User user, Coupon coupon) {
        // Check if the user already has this coupon
        Optional<UserCoupon> existingUserCoupon = userCouponRepository.findByUserAndCoupon(user, coupon);
        if (existingUserCoupon.isPresent()) {
            throw new RuntimeException("이미 가지고 있는 쿠폰입니다..");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);

        return userCouponRepository.save(userCoupon);
    }

    // More methods based on requirements...
}