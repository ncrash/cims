package kr.co.kcs.cims.infra.security.dto;

public record TokenResponse(String accessToken) {}

// refreshToken 추가 시 아래 코드로 대체
// public record TokenResponse(String accessToken, String refreshToken) {}
