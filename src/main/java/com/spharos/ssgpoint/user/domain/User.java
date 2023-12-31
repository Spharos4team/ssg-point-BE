package com.spharos.ssgpoint.user.domain;




import com.spharos.ssgpoint.global.BaseEntity;
import com.spharos.ssgpoint.term.domain.UserTermList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String uuid;

    @Column(length = 45, nullable = false)
    private String loginId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 100)
    private String address;

    @Column(length = 45, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 10, name = "role")
    private Role role;


    @Column(length = 1, nullable = false, columnDefinition = "int default 1")
    private Integer status;

    @Column(length = 100)
    private String pointPassword;


    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "user_term_id")
    private UserTermList term;




    public void updateUserInfo(String address, String email){
        this.address =address;
        this.email = email;
    }

    public void hashPassword(String password){
        this.password = new BCryptPasswordEncoder().encode(password);
    }

    public void changeStatus(Integer status){
        this.status = status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return uuid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
