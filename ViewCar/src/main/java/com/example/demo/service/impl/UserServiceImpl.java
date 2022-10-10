package com.example.demo.service.impl;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.model.response.Data;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    public UserServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public Data login(UserLoginDto userLoginDto) {
        // Xác thực thông tin người dùng Request lên
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDto.getUsername(),
                        userLoginDto.getPassword()
                )
        );

        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Trả về jwt cho người dùng.
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        if (customUserDetails.getUser().getExpiryDatePassword() != null && checkExpiryDate(customUserDetails.getUser().getExpiryDatePassword()))
            return new Data(false, "password expiration", null);

        String token = "Bearer " + tokenProvider.generateToken(customUserDetails);
        return new Data(true, "token", token);
    }

    @Override
    public Data register(UserDto userDto, StringBuffer request) {
        if (userRepository.existsByUsername(userDto.getUsername()))
            return new Data(false, "username already exists", null);
        if (userRepository.existsByEmail(userDto.getEmail()))
            return new Data(false, "email already exists", null);

        UserEntity userEntity = new UserEntity().mapperDto(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.setVerificationCode(RandomString.make(20));
        userEntity.setExpiryDate(new Date());

        userRepository.save(userEntity);

//        send mail
        Map<String, Object> props = new HashMap<>();
        props.put("name", userDto.getUsername());
        props.put("url", request.append(userEntity.getVerificationCode()));

        try {
            mailService.sendMail(props, userDto.getEmail(), "register", "Xác thực tài khoản");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return new Data(true, "send email success", userEntity.getVerificationCode());
    }

    @Override
    public Data veryficationCode(String code) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByVerificationCode(code);
        if (optionalUserEntity.isEmpty())
            return new Data(false, "verification Code not found", null);

        UserEntity userEntity = optionalUserEntity.get();

        if (checkExpiryDate(userEntity.getExpiryDate()))
            return new Data(false, "token expiration", null);

        userEntity.setIsEnable(true);
        userEntity.setVerificationCode(null);
        userEntity.setExpiryDate(null);

        return new Data(true, "register success", modelMapper.map(userRepository.save(userEntity), UserDto.class));
    }

    @Override
    public Data changePassword(String oldPassword, String newPassword) {
        UserEntity userEntity = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUser();

        if (!passwordEncoder.matches(oldPassword, userEntity.getPassword()))
            return new Data(false, "mat khau khong dung", null);

        if (oldPassword.equals(newPassword))
            return new Data(false, "mat khau moi phai khac mat khau cu", null);

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        if (userEntity.getExpiryDatePassword() != null) {
            userEntity.setExpiryDatePassword(null);
        }

        return new Data(true, "change password success", modelMapper.map(userRepository.save(userEntity), UserDto.class));
    }

    @Override
    public Data forgotPassword(String email) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(email);
        if (optionalUserEntity.isEmpty())
            return new Data(false, "email not found", null);

        String newPassword = RandomString.make(10);

        UserEntity userEntity = optionalUserEntity.get();
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userEntity.setExpiryDatePassword(new Date());

//        send mail
        Map<String, Object> props = new HashMap<>();
        props.put("username", userEntity.getUsername());
        props.put("password", newPassword);

        try {
            mailService.sendMail(props, userEntity.getEmail(), "forgotPassword", "Quên mật khẩu");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return new Data(true, "forgot password success", modelMapper.map(userRepository.save(userEntity), UserDto.class));
    }

    private Boolean checkExpiryDate(Date timeDB) {
        return System.currentTimeMillis() > timeDB.getTime() + 300000L; //wa 5p k xac thuc
    }
}
