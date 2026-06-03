package com.payplux.service.impl;

import com.payplux.model.AuthSession;
import com.payplux.repository.AuthSessionRepository;
import com.payplux.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthSessionRepository authSessionRepository;


    @Override
    @Transactional
    public void revokeSession(Long userId, Long sessionId) {

        AuthSession authSession = authSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 🔥 STRICT OWNERSHIP CHECK
        if (!authSession.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: session does not belong to user");
        }

        authSession.setRevoked(true);
        authSession.setLastActiveAt(LocalDateTime.now());
        authSessionRepository.save(authSession);
    }
}
