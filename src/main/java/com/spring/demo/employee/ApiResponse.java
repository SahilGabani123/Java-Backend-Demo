package com.spring.demo.employee;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "status", "message", "data", "meta", "errors" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    @JsonProperty("meta")
    private PageMeta meta;

    @JsonProperty("errors")
    private Map<String, String> errors;

    public ApiResponse() {
    }

    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    // ✅ success with data
    public ApiResponse(boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // ✅ success with data + meta
    public ApiResponse(boolean status, String message, T data, PageMeta meta) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.meta = meta;
    }

    // ✅ error response
    public ApiResponse(boolean status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    // getters
    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public PageMeta getMeta() {
        return meta;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
