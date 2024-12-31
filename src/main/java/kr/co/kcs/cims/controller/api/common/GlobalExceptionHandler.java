package kr.co.kcs.cims.controller.api.common;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @NonNull
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<CustomErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new CustomErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue() != null
                                ? error.getRejectedValue().toString()
                                : "",
                        error.getDefaultMessage()))
                .toList();

        return new CustomErrorResponse("INVALID_INPUT", "입력값이 올바르지 않습니다", fieldErrors);
    }
}
