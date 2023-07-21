package com.backend.api.security.error;

public class UnprocessableException extends Exception {

    public UnprocessableException() {
        super();
    }

    public UnprocessableException(String message) {
        super(message);
    }


    public UnprocessableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnprocessableException(Throwable cause) {
        super(cause);
    }

    protected UnprocessableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
