package com.helper.backend.domain.user.service;

import com.helper.backend.domain.user.dto.LoginRequest;
import com.helper.backend.domain.user.dto.LoginResponse;
import com.helper.backend.domain.user.dto.SignupRequest;
import com.helper.backend.domain.user.dto.UpdatePasswordRequest;
import com.helper.backend.domain.user.dto.UpdateUserRequest;
import com.helper.backend.domain.user.entity.User;
import com.helper.backend.domain.user.repository.UserRepository;
import com.helper.backend.global.exception.CustomException;
import com.helper.backend.global.exception.ErrorCode;
import com.helper.backend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  // 회원가입
  @Transactional
  public void signup(SignupRequest request) {
    // 이메일 중복 체크
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .nickname(request.getNickname())
        .build();

    userRepository.save(user);
  }

  // 로그인
  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest request) {
    // 이메일 존재 여부 확인
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    // 비밀번호 검증
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.INVALID_PASSWORD);
    }

    // JWT 토큰 생성
    String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());

    return new LoginResponse(accessToken, user.getId(), user.getEmail(), user.getNickname());
  }

  // 닉네임 수정
  @Transactional
  public void updateUser(Long userId, UpdateUserRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.updateNickname(request.getNickname());
  }

  // 회원탈퇴
  @Transactional
  public void deleteUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    user.softDelete();
  }

  // 비밀번호 변경
  @Transactional
  public void updatePassword(Long userId, UpdatePasswordRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.INVALID_PASSWORD);
    }

    user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
  }
}