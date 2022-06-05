package woowacourse.shoppingcart.ui;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import woowacourse.auth.dto.ExceptionResponse;
import woowacourse.exception.InvalidAuthException;
import woowacourse.exception.InvalidCustomerException;

@RestControllerAdvice(basePackages = "woowacourse.shoppingcart")
public class CartControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> loginExceptionHandler(InvalidAuthException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidRequest(final BindingResult bindingResult) {
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        final FieldError mainError = fieldErrors.get(0);
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(mainError.getDefaultMessage()));
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        ConstraintViolationException.class,
    })
    public ResponseEntity<ExceptionResponse> handleInvalidRequest(final RuntimeException exception) {
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler({
        InvalidCustomerException.class,
    })
    public ResponseEntity<ExceptionResponse> handleInvalidAccess(final RuntimeException exception) {
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleNotFound(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ExceptionResponse(exception));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleUnhandledException(RuntimeException exception) {
        return ResponseEntity.badRequest()
            .body(new ExceptionResponse(exception));
    }
}
