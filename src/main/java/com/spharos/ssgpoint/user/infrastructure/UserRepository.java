package com.spharos.ssgpoint.user.infrastructure;

import com.spharos.ssgpoint.point.domain.Point;
import com.spharos.ssgpoint.term.domain.UserTermList;
import com.spharos.ssgpoint.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom{
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByUuid(String UUID);
    Optional<User> findByBarCode(String barCode);

    @Query("SELECT u.term FROM User u  WHERE u.uuid = :uuid")
    Optional<UserTermList> findTermJsonByUuid(@Param("uuid") String uuid);

    @Query("SELECT p from Point p join p.user u on u.id = p.user.id where u.uuid = :uuid order by p.createdDate desc limit 1")
    Point findTotalByUuid(@Param("uuid") String uuid);



}
