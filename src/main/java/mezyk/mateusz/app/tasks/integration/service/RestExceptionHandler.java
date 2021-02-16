package mezyk.mateusz.app.tasks.integration.service;

import mezyk.mateusz.app.tasks.core.data.exception.InvalidTaskRequestException;
import mezyk.mateusz.app.tasks.core.data.exception.TaskDataViolationException;
import mezyk.mateusz.app.tasks.core.data.exception.TaskNotFoundException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    protected ResponseEntity<Object> handleTaskNotFound(TaskNotFoundException ex) {
        RestExceptionInfo restException = new RestExceptionInfo(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(restException, restException.getStatus());
    }

    @ExceptionHandler(TaskDataViolationException.class)
    protected ResponseEntity<Object> handleDataViolation(TaskDataViolationException ex) {
        RestExceptionInfo restException = new RestExceptionInfo(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(restException, restException.getStatus());
    }

    @ExceptionHandler(InvalidTaskRequestException.class)
    protected ResponseEntity<Object> handleDataViolation(InvalidTaskRequestException ex) {
        RestExceptionInfo restException = new RestExceptionInfo(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(restException, restException.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        RestExceptionInfo restException = new RestExceptionInfo(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(restException, restException.getStatus());
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    protected ResponseEntity<Object> handleDataViolation(InvalidDataAccessApiUsageException ex) {
        RestExceptionInfo restException = new RestExceptionInfo(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(restException, restException.getStatus());
    }

}
