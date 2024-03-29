package com.backend.api.security.error;

public class ForbiddenException extends Exception {

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }


    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
