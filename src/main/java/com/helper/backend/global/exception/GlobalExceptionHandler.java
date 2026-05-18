// global/exception/GlobalExceptionHandler.java
package com.helper.backend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 커스텀 예외 처리
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    log.error("CustomException: {}", e.getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getHttpStatus())
        .body(new ErrorResponse(e.getErrorCode()));
  }

  // @Valid 검증 실패 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException e) {
    FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
    log.error("ValidationException: {}", fieldError.getDefaultMessage());
    ErrorCode errorCode = ErrorCode.INVALID_INPUT;
    return ResponseEntity
        .status(errorCode.getHttpStatus())
        .body(new ErrorResponse(errorCode));
  }

  // 나머지 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Exception: {}", e.getMessage());
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
        .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}