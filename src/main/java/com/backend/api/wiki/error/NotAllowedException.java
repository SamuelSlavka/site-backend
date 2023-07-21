package com.backend.api.wiki.error;

public class NotAllowedException extends Exception {
    public NotAllowedException() {
        super();
    }

    public NotAllowedException(String message) {
        super(message);
    }

    public NotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAllowedException(Throwable cause) {
        super(cause);
    }

    protected NotAllowedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}