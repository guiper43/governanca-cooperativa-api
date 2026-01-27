package br.com.guilherme.governanca_cooperativa_api.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorDto> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ApiErrorDto("REGRA_DE_NEGOCIO", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDto> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String codigo = status.name();
        String mensagem = ex.getReason() != null ? ex.getReason() : "Erro";
        return ResponseEntity.status(status)
            .body(new ApiErrorDto(codigo, mensagem, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .orElse("Dados inválidos");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorDto("VALIDACAO", mensagem, request.getRequestURI()));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiErrorDto> handleCpfNotFound(FeignException.NotFound ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorDto("CPF_NAO_ENCONTRADO", "CPF inválido ou não encontrado na base externa", request.getRequestURI()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConflictApiErrorDtoResponseEntity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiErrorDto("CONFLITO", "Violação de integridade", request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericApiErrorDtoResponseEntity(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorDto("ERRO_INTERNO", "Ocorreu um erro inesperado", request.getRequestURI()));
    }


}