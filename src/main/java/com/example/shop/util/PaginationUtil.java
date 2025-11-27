package com.example.shop.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    public static Pageable defaultPageable(Integer page, Integer size) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size <= 0 || size > 100) ? 20 : size;
        return PageRequest.of(p, s);
    }
}
