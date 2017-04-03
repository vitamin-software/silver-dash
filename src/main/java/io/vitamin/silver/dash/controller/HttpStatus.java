package io.vitamin.silver.dash.controller;


public enum HttpStatus {
    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    INTERNAL_ERROR(500);

    private int intValue;

    private HttpStatus(int intValue) {
        this.intValue = intValue;
    }

    public int intValue() {
        return this.intValue;
    }
}
