package com.payplux.controller;

import com.payplux.dto.request.LoginRequest;
import com.payplux.dto.response.AuthResponse;
import com.payplux.model.Role;
import com.payplux.model.UserModel;
import com.payplux.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;



    @GetMapping("/admin/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        UserModel user = userService.getById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserProfileById(@PathVariable Long id) {
        UserModel user = userService.getById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserModel> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(userService.getByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<Optional<UserModel>> getByEmail(@PathVariable String email) {
        Optional<UserModel> user = userService.getByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }





}
