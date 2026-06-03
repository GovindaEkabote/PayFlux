package com.payplux.repository;

import com.payplux.model.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    List<AuthSession> findByUserIdAndIsRevokedFalse(Long userId);

    Optional<AuthSession> findByRefreshToken(String refreshToken);
}
