package com.mrkresnofatihdev.gulugulu.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrkresnofatihdev.gulugulu.exceptions.NotMatchingResourceNameException;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.utilities.ResponseHelper;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @Autowired
    private Tracer tracer;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));

        var traceId = tracer.currentSpan().context().traceId();

        var returnErrorMessage = String.format("Invalid Request: %s | Corr: %s", errors, traceId);

        logger.error(String.format("MethodArgumentNotValid exception caused possibly due to invalid request body: %s", errors));
        return new ResponseEntity<>(new ResponseModel<String>(null, returnErrorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotMatchingResourceNameException.class})
    public ResponseEntity<Object> handleNotMatchingResourceNameException() {
        logger.error("ResourceName provided not valid for the provided request body/params");
        var traceId = tracer.currentSpan().context().traceId();
        var returnErrorMessage = String.format("Authorization Error! | Corr: %s", traceId);
        return new ResponseEntity<>(new ResponseModel<String>(null, returnErrorMessage), HttpStatus.BAD_REQUEST);
    }
}
