package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "username khong dc de trong")
//    @Pattern(regexp = "(^[a-z]+[0-9]*[A-Z]*).{2,18}",
//    message = "username bắt đầu bằng chữ, độ dài từ ít nhất 2, nhiều nhất 18 ký tự")
    private String username;

    @NotBlank(message = "password khong dc de trong")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{4,20}$",
//            message = "password phải chứa ít nhất một số, một chữ viết thường, một chữ viết hoa, một ký tự đặc biệt, độ dài từ ít nhất 4, nhiều nhất 20 ký tự")
    private String password;

    @NotBlank(message = "email khong dc de trong")
    @Email(message = "email khong hop le")
    private String email;
}
