package io.swiczka.github.apiorder;

import io.swiczka.github.apiorder.exceptions.ForbiddenException;
import io.swiczka.github.sharedcommon.BaseGlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(final ForbiddenException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }
}
