package com.elyashevich.users.api.controller;

import com.elyashevich.users.api.dto.ExceptionBodyDto;
import com.elyashevich.users.exception.ResourceAlreadyExistsException;
import com.elyashevich.users.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    private static final String NOT_SUPPORTED_MESSAGE = "Http method with this URL not found.";
    private static final String FAILED_VALIDATION_MESSAGE = "Validation failed.";
    private static final String UNEXPECTED_ERROR_MESSAGE = "Something went wrong.";
    private static final String NOT_FOUND_MESSAGE = "Resource was not found.";
    private static final String RESOURCE_ALREADY_EXISTS_MESSAGE = "Resource already exists.";
    private static final String REQUEST_MUST_CONTAINS_BODY_MESSSAGE = "Request must contains body";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionBodyDto handleResourceNotFoundException(
            final ResourceNotFoundException exception
    ) {
        return this.handleException(exception, NOT_FOUND_MESSAGE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ExceptionBodyDto handleResourceAlreadyExistsException(
            final ResourceAlreadyExistsException exception
    ) {
        return this.handleException(exception, RESOURCE_ALREADY_EXISTS_MESSAGE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ExceptionBodyDto handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException exception
    ) {
        return this.handleException(exception, REQUEST_MUST_CONTAINS_BODY_MESSSAGE);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ExceptionBodyDto handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException exception
    ) {
        return this.handleException(exception, NOT_SUPPORTED_MESSAGE);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(NoResourceFoundException.class)
    public ExceptionBodyDto handleNoResourceFoundException(
            final NoResourceFoundException exception
    ) {
        return this.handleException(exception, NOT_SUPPORTED_MESSAGE);
    }

    @SuppressWarnings("all")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionBodyDto handleMethodArgumentNotValidException
            (final MethodArgumentNotValidException exception
            ) {
        var errors = exception.getBindingResult()
                .getFieldErrors().stream()
                .collect(Collectors.toMap(
                                FieldError::getField,
                                fieldError -> fieldError.getDefaultMessage(),
                                (exist, newMessage) -> exist + " " + newMessage + "."
                        )
                );
        return new ExceptionBodyDto(FAILED_VALIDATION_MESSAGE, errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBodyDto handleException(final Exception exception) {
        return this.handleException(exception, UNEXPECTED_ERROR_MESSAGE);
    }

    private ExceptionBodyDto handleException(final Exception exception, final String defaultMessage) {
        var message = exception.getMessage() == null ? defaultMessage : exception.getMessage();
        log.warn("{} '{}'.", defaultMessage, message);
        return new ExceptionBodyDto(message);
    }
}