package br.com.guilherme.governanca_cooperativa_api.web.dto.sessao;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SessaoRequest(
    @NotNull
    UUID pautaId,
Integer duracaoMinutos    ) {
}
