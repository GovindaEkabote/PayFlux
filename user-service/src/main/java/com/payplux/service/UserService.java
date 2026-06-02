package com.payplux.service;

import com.payplux.model.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserModel registerUser(UserModel userModel);

    Optional<UserModel> getByEmail(String email);

    Optional<UserModel> getByPhone(String phone);

    UserModel getById(Long id);

    List<UserModel> getAllUsers();

    void deleteUser(Long id);
}