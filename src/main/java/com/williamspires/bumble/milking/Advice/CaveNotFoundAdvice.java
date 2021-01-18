package com.williamspires.bumble.milking.Advice;

import com.williamspires.bumble.milking.Exceptions.CaveNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CaveNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(CaveNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String caveNotFoundException(CaveNotFoundException ex) {
        log.error(ex.getMessage());
        return ex.getMessage();
    }
}
