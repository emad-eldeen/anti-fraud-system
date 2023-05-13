package com.transfers.antifraud.businesslayer.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserConflictException extends RuntimeException {
    final String message;
}
