// global/exception/ErrorResponse.java
package com.helper.backend.global.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

  private final String code;
  private final String message;

  public ErrorResponse(ErrorCode errorCode) {
    this.code = errorCode.getCode();
    this.message = errorCode.getMessage();
  }
}