package com.manchui.global.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String context, String service, String errorMessage) {

        super(String.format("[%s][%s] %s", context, service, errorMessage));
    }

}
