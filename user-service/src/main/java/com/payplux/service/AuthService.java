package com.payplux.service;

public interface AuthService {
    void revokeSession(Long userId, Long sessionId);

}
