package com.example.demo.controller;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.response.Data;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Data> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        Data data = userService.login(userLoginDto);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    @PostMapping("/register")
    public ResponseEntity<Data> register(@Valid @RequestBody UserDto userDto, HttpServletRequest request) {
        Data data = userService.register(userDto, request.getRequestURL().append("/verify?code="));
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }

    @PostMapping("/veryfication_code")
    public ResponseEntity<Data> veryficationCode(@RequestParam String code) {
        Data data = userService.veryficationCode(code);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }

    @PostMapping("/change_password")
    public ResponseEntity<Data> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Data data = userService.changePassword(oldPassword, newPassword);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<Data> forgotPassword(@RequestParam String email) {
        Data data = userService.forgotPassword(email);
        if (data.isSuccess()) return ResponseEntity.ok(data);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
    }
}

