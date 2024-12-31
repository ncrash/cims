package kr.co.kcs.cims.controller.api.common;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.web.ErrorResponse;

public record CustomErrorResponse(String code, String message, List<FieldError> errors) implements ErrorResponse {

    @Override
    @NonNull
    public ProblemDetail getBody() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(getStatusCode().value());
        problemDetail.setTitle(message);
        problemDetail.setProperty("code", code);
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    public record FieldError(String field, String value, String reason) {}
}
