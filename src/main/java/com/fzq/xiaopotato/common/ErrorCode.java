package com.fzq.xiaopotato.common;

/**
 * error code
 *
 */
public enum ErrorCode {

    SUCCESS(200, "ok", ""),
    PARAMS_ERROR(40000, "Request parameter error", ""),
    NULL_ERROR(40001, "Request data null", ""),
    NOT_LOGIN(40100, "not login", ""),
    NO_AUTH(40101, "not authenticated", ""),

    FORBIDDEN(40301, "forbid to operate", ""),
    SYSTEM_ERROR(50000, "system error", "");

    private final int code;

    /**
     * state code msg
     */
    private final String message;

    /**
     * description
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public String getDescription() {
        return description;
    }
}
