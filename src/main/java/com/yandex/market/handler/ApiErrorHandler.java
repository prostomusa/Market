package com.yandex.market.handler;

import com.yandex.market.exception.IncorrectDataException;
import com.yandex.market.exception.NotFoundException;
import com.yandex.market.model.Error;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new Error(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Error> handleNotFoundException() {
        return new ResponseEntity<>(new Error(404, "Item not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {IncorrectDataException.class})
    public ResponseEntity<Error> handleIncorrectDataException(IncorrectDataException e) {
        return new ResponseEntity<>(new Error(400, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
