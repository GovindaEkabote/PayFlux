package com.payplux.repository;

import com.payplux.model.RefreshToken;
import com.payplux.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token) ;
    void deleteByUser(UserModel user) ;

    Optional<RefreshToken> findByUser(UserModel user);
}
