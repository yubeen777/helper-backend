package com.helper.backend.domain.user.controller;

import com.helper.backend.domain.user.dto.UpdatePasswordRequest;
import com.helper.backend.domain.user.dto.UpdateUserRequest;
import com.helper.backend.domain.user.service.UserService;
import com.helper.backend.global.jwt.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class MeController {

  private final UserService userService;
  private final JwtUtil jwtUtil;

  // 닉네임 수정
  @PatchMapping("/me")
  public ResponseEntity<Void> updateMe(
      @RequestHeader("Authorization") String token,
      @Valid @RequestBody UpdateUserRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    userService.updateUser(userId, request);
    return ResponseEntity.ok().build();
  }

  // 회원탈퇴
  @DeleteMapping("/me")
  public ResponseEntity<Void> deleteMe(
      @RequestHeader("Authorization") String token) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  // 비밀번호 변경
  @PatchMapping("/me/password")
  public ResponseEntity<Void> updatePassword(
      @RequestHeader("Authorization") String token,
      @Valid @RequestBody UpdatePasswordRequest request) {

    Long userId = jwtUtil.getUserId(token.substring(7));
    userService.updatePassword(userId, request);
    return ResponseEntity.ok().build();
  }
}