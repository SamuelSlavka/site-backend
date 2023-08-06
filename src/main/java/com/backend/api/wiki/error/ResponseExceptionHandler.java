package com.backend.api.wiki.error;

import com.backend.api.core.error.entity.ErrorMessage;
import com.backend.api.security.error.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@ResponseStatus
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotFoundException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorMessage> forbiddenException(ForbiddenException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.FORBIDDEN, exception.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotAllowedException exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(message);
    }

}
