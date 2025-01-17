package com.jakub.xmlreader.exception;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.xml.core.AttributeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.concurrent.ExecutionException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
   *
   * @param ex      MissingServletRequestParameterException
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {
    String error = ex.getParameterName() + " parameter is missing";
    return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
  }

  /**
   * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
   *
   * @param ex      HttpMediaTypeNotSupportedException
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getContentType());
    builder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
    return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
  }

  /**
   * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
   *
   * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
    apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
    return buildResponseEntity(apiError);
  }

  /**
   * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
   *
   * @param ex the ConstraintViolationException
   * @return the ApiError object
   */
  @ExceptionHandler(javax.validation.ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
      javax.validation.ConstraintViolationException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("Validation error");
    apiError.addValidationErrors(ex.getConstraintViolations());
    return buildResponseEntity(apiError);
  }

  /**
   * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
   *
   * @param ex      HttpMessageNotReadableException
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;
    log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
    String error = "Malformed JSON request";
    return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
  }

  /**
   * Handle HttpMessageNotWritableException.
   *
   * @param ex      HttpMessageNotWritableException
   * @param headers HttpHeaders
   * @param status  HttpStatus
   * @param request WebRequest
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String error = "Error writing JSON output";
    return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
  }

  /**
   * Handle NoHandlerFoundException.
   *
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return
   */
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * Handle generic MethodArgumentTypeMismatchException.class
   *
   * @param ex the Exception
   * @return the ApiError object
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      WebRequest request) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
        ex.getName(),
        ex.getValue(),
        ex.getRequiredType().getSimpleName()));
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * Handle HttpRequestMethodNotSupportedException
   *
   * @param ex the Exception
   * @return the ApiError object
   */
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(
        "  ");
    ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

    String message = """
        method is not supported for this request.
        Supported methods are:  
        """ + ex.getSupportedHttpMethods().toString();
    
    ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, message, ex);
    return new ResponseEntity<Object>(
        apiError, new HttpHeaders(), apiError.getStatus());
  }

  /**
   * Handle ExecutionException.class
   *
   * @param ex the Exception
   * @return the ApiError object
   */
  @ExceptionHandler(ExecutionException.class)
  protected ResponseEntity<Object> handleExecutionException(
      ExecutionException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("There was an execution error while parse XML file");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  /**
   * Handle InterruptedException.class
   *
   * @param ex the Exception
   * @return the ApiError object
   */
  @ExceptionHandler(InterruptedException.class)
  protected ResponseEntity<Object> handleInterruptedException(
      InterruptedException ex) {
    ApiError apiError = new ApiError(BAD_REQUEST);
    apiError.setMessage("There was an interrupted error while parse XML file");
    apiError.setDebugMessage(ex.getMessage());
    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

}