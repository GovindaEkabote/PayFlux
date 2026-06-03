package com.payplux.service;

import com.payplux.model.RefreshToken;
import com.payplux.model.UserModel;

import java.util.Optional;


public interface RefreshTokenService {
    RefreshToken createRefreshToken(UserModel user);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(UserModel user);
}
