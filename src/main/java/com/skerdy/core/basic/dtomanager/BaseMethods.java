package com.skerdy.core.basic.dtomanager;

public enum BaseMethods {
    FIND_ALL("findAll"),
    FIND_BY_ID("findById"),
    INPUT("input");

    private final String method;

    BaseMethods(String method) {
        this.method = method;
    }

    public String value() {
        return method;
    }
}
