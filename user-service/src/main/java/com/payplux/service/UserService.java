package com.payplux.service;

import com.payplux.dto.request.LoginRequest;
import com.payplux.dto.response.AuthResponse;
import com.payplux.model.Role;
import com.payplux.model.UserModel;
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

    AuthResponse login(LoginRequest loginRequest);


    Optional<UserModel> getCurrentUser(String token);

    UserModel updateUserRole(Long userId, Role role);
}