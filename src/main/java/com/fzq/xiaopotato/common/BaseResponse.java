package com.fzq.xiaopotato.common;

import lombok.Data;

import java.io.Serializable;

/**
 * common return class
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * state code
     */
    private int code;

    /**
     * data
     */
    private T data;

    /**
     * msg
     */
    private String message;

    /**
     * description
     */
    private String description;

    public BaseResponse() {

    }

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}