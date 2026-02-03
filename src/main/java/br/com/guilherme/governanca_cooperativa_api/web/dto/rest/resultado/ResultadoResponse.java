package br.com.guilherme.governanca_cooperativa_api.web.dto.rest.resultado;

import br.com.guilherme.governanca_cooperativa_api.domain.enums.rest.ResultadoStatus;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ResultadoResponse(
        @Schema(description = "Identificador da pauta consultada", example = "550e8400-e29b-41d4-a716-446655440000") UUID pautaId,

        @Schema(description = "Total de votos 'SIM'", example = "150") long totalSim,

        @Schema(description = "Total de votos 'NÃO'", example = "75") long totalNao,

        @Schema(description = "Status final da votação. Valores: APROVADA, REPROVADA, EMPATE, EM_ANDAMENTO", example = "APROVADA") ResultadoStatus status) {
}
