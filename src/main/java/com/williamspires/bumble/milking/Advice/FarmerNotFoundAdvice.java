package com.williamspires.bumble.milking.Advice;

import com.williamspires.bumble.milking.Exceptions.FarmerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class FarmerNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(FarmerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String farmerNotFoundException(FarmerNotFoundException ex) {
        log.error(ex.getMessage());
        return ex.getMessage();
    }
}
