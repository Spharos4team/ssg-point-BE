package com.spharos.ssgpoint.user.infrastructure;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.Year;
import java.util.List;

import static com.spharos.ssgpoint.point.domain.PointType.*;
import static com.spharos.ssgpoint.point.domain.QPoint.point1;
import static com.spharos.ssgpoint.receipt.domain.QReceipt.receipt;
import static com.spharos.ssgpoint.user.domain.QUser.user;


public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    int currentYear = Year.now().getValue();

    @Override
    public Integer findSavePointByUUID(String uuid) {

        Integer result = queryFactory
                .select(point1.point.sum())
                .from(point1)
                .join(point1.user, user)
                .on(user.uuid.eq(uuid))
                .where(point1.type.eq(결제).
                                or(point1.type.eq(이벤트)),
                        point1.createdDate.year().eq(currentYear))
                .fetchOne();

        return result != null ? result : 0;

    }

    @Override
    public Integer findUsePointByUUID(String uuid) {
        Integer result = queryFactory
                .select(point1.point.sum())
                .from(point1)
                .join(point1.user, user)
                .on(user.uuid.eq(uuid))
                .where(point1.type.eq(이벤트),
                        point1.createdDate.year().eq(currentYear))
                .fetchOne();
        return result != null ? result : 0;
    }

    @Override
    public Long findVisitDateByReceipt(String uuid) {
        Long result = queryFactory
                .select(point1.receipt.count())
                .from(point1)
                .join(point1.user, user)
                .join(point1.receipt, receipt)
                .where(user.uuid.eq(uuid).and((point1.type.eq(결제)
                                .or(point1.type.eq(이벤트)))))
                .fetchOne();
        return result != null ? result : 0;
    }

    @Override
    public Integer findTotalPointByReceipt(String uuid) {
        Integer result = queryFactory
                .select(point1.receipt.amount.sum())
                .from(point1)
                .join(point1.user, user)
                .on(user.uuid.eq(uuid))
                .where(point1.type.eq(결제)
                        .or(point1.type.eq(이벤트)))
                .fetchOne();
        return result != null ? result : 0;
    }

    @Override
    public List<Tuple> findCountListTop3ByUUID(String uuid) {
        return queryFactory
                .select(point1.receipt.alliance,point1.receipt.alliance.count())
                .from(point1)
                .join(point1.user, user)
                .on(user.uuid.eq(uuid))
                .where(point1.type.eq(결제)
                        .or(point1.type.eq(이벤트)))
                .groupBy(point1.receipt.alliance)
                .orderBy(receipt.alliance.count().desc())
                .limit(3)
                .fetch();
    }

    @Override
    public List<Tuple> findSumListTop3ByUUID(String uuid) {
        return queryFactory
                .select(point1.receipt.alliance,point1.receipt.amount.sum())
                .from(point1)
                .join(point1.user, user)
                .on(user.uuid.eq(uuid))
                .where(point1.type.eq(결제)
                        .or(point1.type.eq(이벤트)))
                .groupBy(point1.receipt.alliance)
                .orderBy(receipt.alliance.count().desc())
                .limit(3)
                .fetch();
    }


}
