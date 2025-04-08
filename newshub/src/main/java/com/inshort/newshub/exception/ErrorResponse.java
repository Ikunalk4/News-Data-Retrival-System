package com.inshort.newshub.exception;

import java.util.Date;

import lombok.Data;

@Data
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String description;
    private Date timestamp;

    public ErrorResponse(int statusCode, String message, String description) {
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
        this.timestamp = new Date();
    }

}

