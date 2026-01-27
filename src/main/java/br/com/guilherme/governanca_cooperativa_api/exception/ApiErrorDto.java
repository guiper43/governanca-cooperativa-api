package br.com.guilherme.governanca_cooperativa_api.exception;

import java.time.LocalDateTime;

public record ApiErrorDto(
    String codigo,
    String mensagem,
    LocalDateTime timestamp,
    String path) {
    public ApiErrorDto(String codigo, String mensagem, String path) {
        this(codigo, mensagem, LocalDateTime.now(), path);
    }
}
