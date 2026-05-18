// global/exception/ErrorCode.java
package com.helper.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // 유저
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 유저입니다."),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_002", "이미 사용 중인 이메일입니다."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER_003", "비밀번호가 일치하지 않습니다."),

  // 운동 기록
  WORKOUT_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKOUT_001", "존재하지 않는 운동 기록입니다."),
  WORKOUT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "WORKOUT_002", "본인의 운동 기록만 접근할 수 있습니다."),
  WORKOUT_SET_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKOUT_003", "존재하지 않는 세트입니다."),

  // 운동 종목
  EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXERCISE_001", "존재하지 않는 운동 종목입니다."),

  // AI 피드백
  FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK_001", "존재하지 않는 피드백입니다."),
  FEEDBACK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FEEDBACK_002", "본인의 피드백만 조회할 수 있습니다."),

  // 인증
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "만료된 토큰입니다."),

  // 공통
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 내부 오류입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  ErrorCode(HttpStatus httpStatus, String code, String message) {
    this.httpStatus = httpStatus;
    this.code = code;
    this.message = message;
  }
}