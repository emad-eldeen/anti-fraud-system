package com.transfers.antifraud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String cause) {
        super(cause);
    }
}
