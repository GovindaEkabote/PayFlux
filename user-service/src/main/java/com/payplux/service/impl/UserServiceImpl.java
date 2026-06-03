package com.payplux.service.impl;

import com.payplux.dto.request.LoginRequest;
import com.payplux.dto.response.AuthResponse;
import com.payplux.dto.response.SessionResponse;
import com.payplux.exception.custom.DuplicateResourceException;
import com.payplux.exception.custom.InvalidRequestException;
import com.payplux.exception.custom.UserNotFoundException;
import com.payplux.model.AuthSession;
import com.payplux.model.RefreshToken;
import com.payplux.model.Role;
import com.payplux.model.UserModel;
import com.payplux.repository.AuthSessionRepository;
import com.payplux.repository.UserRepository;
import com.payplux.security.JwtUtil;
import com.payplux.service.RefreshTokenService;
import com.payplux.service.UserService;
import com.payplux.util.DeviceUtils;
import com.payplux.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;

    private final AuthSessionRepository authSessionRepository;


    @Override
    public UserModel registerUser(UserModel userModel) {

        if (userRepository.findByEmail(userModel.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exists");
        }

        if (userRepository.findByPhone(userModel.getPhone()).isPresent()){
            throw new DuplicateResourceException("Phone already exists");
        }

        // ✅ Encode password
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userModel.setRole(Role.USER);
        return userRepository.save(userModel);
    }

    @Override
    public Optional<UserModel> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserModel> getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public UserModel getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );

        userRepository.delete(user);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest, String ip, String device) {

        UserModel user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthSession authSession = new AuthSession();
        authSession.setUserId(user.getId());
        authSession.setRefreshToken(refreshToken.getToken());
        authSession.setIpAddress(ip);
        authSession.setDeviceInfo(device);
        authSession.setLastActiveAt(LocalDateTime.now());
        authSession.setRevoked(false);

        authSessionRepository.save(authSession);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .role(user.getRole().name())
                .build();
    }


    @Override
    public Optional<UserModel> getCurrentUser(String token) {
        String email = jwtUtil.extractEmail(token);
        System.out.println(email);
        return userRepository.findByEmail(email);
    }

    public UserModel updateUserRole(Long userId, Role role){
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public AuthResponse refreshToken(String requestToken) {

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        UserModel user = refreshToken.getUser();

        String accessToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(requestToken)
                .role(user.getRole().name())
                .build();
    }

    @Override
    public List<SessionResponse> getActiveSessions(Long userId) {
        return authSessionRepository
                .findByUserIdAndIsRevokedFalse(userId)
                .stream()
                .map(authSession -> new SessionResponse(
                        authSession.getId(),
                        authSession.getDeviceInfo(),
                        authSession.getIpAddress(),
                        authSession.getLastActiveAt()
                ))
                .toList();
    }


}
