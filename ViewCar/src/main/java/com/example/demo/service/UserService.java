package com.example.demo.service;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.response.Data;

public interface UserService {
    Data login(UserLoginDto userLoginDto);

    Data register(UserDto userDto, StringBuffer request);

    Data veryficationCode(String code);

    Data changePassword(String oldPassword, String newPassword);

    Data forgotPassword(String email);
}
