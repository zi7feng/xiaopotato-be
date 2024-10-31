package com.fzq.xiaopotato.common;

public enum NotificationType {
    LIKE("like"),
    SAVE("save"),
    FOLLOW("follow");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
