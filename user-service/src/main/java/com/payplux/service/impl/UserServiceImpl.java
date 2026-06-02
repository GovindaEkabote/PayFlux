package com.payplux.service.impl;

import com.payplux.exception.custom.DuplicateResourceException;
import com.payplux.exception.custom.UserNotFoundException;
import com.payplux.model.UserModel;
import com.payplux.repository.UserRepository;
import com.payplux.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserModel registerUser(UserModel userModel) {
        if (userRepository.findByEmail(userModel.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.findByPhone(userModel.getPhone()).isPresent()){
            throw new DuplicateResourceException("Phone already exists");
        }
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
}
