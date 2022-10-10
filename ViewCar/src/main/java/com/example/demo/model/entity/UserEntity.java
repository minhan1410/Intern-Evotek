package com.example.demo.model.entity;

import com.example.demo.constants.RoleEnum;
import com.example.demo.model.dto.UserDto;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "integer default 0")
    private RoleEnum role;

    @Column(columnDefinition = "boolean default false")
    private Boolean isEnable; // luu trang thai tk

    private String verificationCode; // ma kich hoat tai khoan
    private Date expiryDate;
    private Date expiryDatePassword;

    public UserEntity mapperDto(UserDto userDto){
        this.id = null;
        this.username = userDto.getUsername();
        this.email = userDto.getEmail();
        this.role = RoleEnum.USER;
        this.isEnable = false;
        return this;
    }
}
