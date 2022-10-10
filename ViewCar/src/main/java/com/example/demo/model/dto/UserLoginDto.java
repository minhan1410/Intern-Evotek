package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserLoginDto {
    @NotBlank(message = "username khong dc de trong")
    private String username;

    @NotBlank(message = "password khong dc de trong")
    private String password;
}
