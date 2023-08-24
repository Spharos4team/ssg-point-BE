package com.spharos.ssgpoint.user.infrastructure;

import com.spharos.ssgpoint.term.domain.UserTermList;
import com.spharos.ssgpoint.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByUuid(String UUID);
    Optional<User> findByBarCode(String barCode);

/*    @Query("SELECT l.termJson FROM User u join UserTermList l where l.id = : id")
    UserTermList findTermJsonByUuid(@Param("id") Long id);*/

   @Query("SELECT u.term FROM User u WHERE u.uuid = :uuid")
    Optional<UserTermList> findTermJsonByUuid(@Param("uuid") String uuid);

}
