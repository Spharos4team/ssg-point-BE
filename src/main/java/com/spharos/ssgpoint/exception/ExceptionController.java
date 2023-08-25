package com.spharos.ssgpoint.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> userExHandle(IllegalArgumentException e) {
        ErrorResult errorResult = new ErrorResult("error",e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResult> loginExHandle(BadCredentialsException e) {
        ErrorResult errorResult = new ErrorResult("아이디 또는 비밀번호를 확인해 주세요 ", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResult> customExHandle(CustomException e) {
        ErrorResult errorResult = new ErrorResult("커스텀 에러", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResult> jwtExHandle(ExpiredJwtException e) {
        ErrorResult errorResult = new ErrorResult("refresh 토큰 만료", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.FORBIDDEN);
    }

}