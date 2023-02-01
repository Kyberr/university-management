package ua.com.foxminded.university.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultController {
  
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void defaultExceptionHandler(SecurityException e) {
        log.error("The exception during execution of the program", e);
        ModelAndView modelAndView = new ModelAndView();
      //  modelAndView.addObject("url", request.getRequestURI());
        modelAndView.setViewName("error");
    }
}
