package br.com.jhonnyazevedo.timegrid_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Regra de negócio violada",
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> fields = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        ValidationErrorResponse response = new ValidationErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Dados inválidos",
                "Um ou mais campos estão inválidos",
                request.getRequestURI(),
                fields
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Parâmetro inválido",
                "O parâmetro '" + exception.getName() + "' possui um valor inválido",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Requisição inválida",
                "O corpo da requisição está inválido ou mal formatado",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Recurso não encontrado",
                "Não existe endpoint para este caminho",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }
}
