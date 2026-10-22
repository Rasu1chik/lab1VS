package com.example.fantasy.controller;

import com.example.fantasy.exception.ApiException;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

final class HttpPages {
    private HttpPages() {}

    static Pageable page(int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "page должен быть >= 0, size — от 1 до 50");
        }
        return PageRequest.of(page, size);
    }

    static <T> ResponseEntity<List<T>> withTotal(Page<T> page) {
        return ResponseEntity.ok().header("X-Total-Count", Long.toString(page.getTotalElements()))
                .body(page.getContent());
    }

    static <T> ResponseEntity<T> created(String path, Long id, T body) {
        URI location = UriComponentsBuilder.fromPath(path).buildAndExpand(id).toUri();
        return ResponseEntity.created(location).body(body);
    }
}
