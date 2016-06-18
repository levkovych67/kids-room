package ua.softserveinc.tc.controller.util;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import ua.softserveinc.tc.constants.ErrorConstants;
import ua.softserveinc.tc.server.exception.ResourceNotFoundException;

/**
 * Created by Nestor on 18.05.2016.
 * Class serves for global exception handling
 */

@ControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NoHandlerFoundException.class,
            ResourceNotFoundException.class
    })
    public String handleError404() {
        return ErrorConstants.NOT_FOUND_VIEW;
    }

    /**
     * Responds to user with AccessDenied view
     *
     * @return AccessDenied view
     */
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleError403() {
        return ErrorConstants.ACCESS_DENIED_VIEW;
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public String handleError500() {
        return ErrorConstants.NOT_FOUND_VIEW;
    }

}
