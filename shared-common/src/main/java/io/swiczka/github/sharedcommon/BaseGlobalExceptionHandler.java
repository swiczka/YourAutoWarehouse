package io.swiczka.github.sharedcommon;

import java.time.Instant;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseGlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllUncaughtExceptions(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        if (ex instanceof ErrorResponse errorResponse) {
            return errorResponse.getBody(); //returns original exception status from spring
        }



        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred. Please contact support."
        );
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(final ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
        problem.setTitle("Validation Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
