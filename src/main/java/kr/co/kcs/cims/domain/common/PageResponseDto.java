package kr.co.kcs.cims.domain.common;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponseDto<T>(
        List<T> content, int pageNo, int pageSize, long totalElements, int totalPages, boolean last) {
    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
