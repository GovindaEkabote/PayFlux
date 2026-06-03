package com.payplux.controller;

import com.payplux.dto.request.LoginRequest;
import com.payplux.dto.request.RefreshTokenRequest;
import com.payplux.dto.response.AuthResponse;
import com.payplux.exception.custom.UserNotFoundException;
import com.payplux.model.Role;
import com.payplux.model.UserModel;
import com.payplux.security.JwtUtil;
import com.payplux.service.RefreshTokenService;
import com.payplux.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@RequestBody UserModel userModel) {
        UserModel saveUser = userService.registerUser(userModel);

        return new ResponseEntity<>(saveUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                userService.refreshToken(request.getRefreshToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = extractToken(request);
        String email = jwtUtil.extractEmail(token);

        UserModel user = userService.getByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserModel> getCurrentUser(HttpServletRequest request) {

        String token = extractToken(request);

        UserModel userModel = userService.getCurrentUser(token)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @PutMapping("/admin/role/{userId}")
    public ResponseEntity<UserModel> updateUserRole(@PathVariable Long userId,
                                                    @RequestParam  Role role,
                                                    HttpServletRequest request) {
        String token = extractToken(request);
        String email = jwtUtil.extractEmail(token);

        UserModel currentUser = userService.getByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Unauthorized");
        }

        UserModel user = userService.updateUserRole(userId, role);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private String extractToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }
}
