package com.payplux.service;

import com.payplux.dto.request.LoginRequest;
import com.payplux.dto.response.AuthResponse;
import com.payplux.dto.response.SessionResponse;
import com.payplux.model.RefreshToken;
import com.payplux.model.Role;
import com.payplux.model.UserModel;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserModel registerUser(UserModel userModel);

    Optional<UserModel> getByEmail(String email);



    Optional<UserModel> getByPhone(String phone);

    UserModel getById(Long id);

    List<UserModel> getAllUsers();

    void deleteUser(Long id);

    AuthResponse login(LoginRequest loginRequest,  String ip, String device);


    Optional<UserModel> getCurrentUser(String token);

    UserModel updateUserRole(Long userId, Role role);

    AuthResponse refreshToken(String refreshToken);

    List<SessionResponse> getActiveSessions(Long userId);

//    void revokeSession(Long userId, Long sessionId);

}