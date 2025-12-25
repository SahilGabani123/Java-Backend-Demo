package com.spring.demo.employee;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "page",
        "per_page",
        "total",
        "total_pages",
        "next_page_url",
        "prev_page_url"
})
public record PageMeta(
        int page,
        int per_page,
        long total,
        int total_pages,
        String next_page_url,
        String prev_page_url
) {}
